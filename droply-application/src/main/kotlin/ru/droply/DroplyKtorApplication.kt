package ru.droply

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import java.lang.reflect.UndeclaredThrowableException
import java.time.Duration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import ru.droply.middleware.DroplyMiddleware
import ru.droply.service.extensions.auth
import ru.droply.sprintor.event.UserLogoutEvent
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.ktor.retrieveText
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.ExceptionProcessor
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.Scene
import ru.droply.sprintor.scene.SceneManager
import ru.droply.sprintor.scene.SceneRequest
import ru.droply.sprintor.spring.autowired
import ru.droply.sprintor.spring.context

private val sceneManager: SceneManager by autowired()
private val sceneMap: Map<String, Scene<*>> by autowired("sceneMap")

private val middlewareCollection: Collection<DroplyMiddleware> by autowired("middlewareCollection")

private val exceptionProcessor: ExceptionProcessor by autowired()

private val logger = KotlinLogging.logger {}

private val eventPublisher: ApplicationEventPublisher by context()

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // Setting up scenes
    sceneMap.forEach { (path, scene) ->
        sceneManager[path] = scene
        logger.info { "Registered scene $path: ${scene::class}" }
    }

    routing {
        webSocket("/") { serveDroplyWebsocket() }
    }
}

private suspend fun DefaultWebSocketSession.serveDroplyWebsocket() {
    val session = this
    incoming.retrieveText {
        val text = readText()
        try {
            val sceneRequest = Json.decodeFromString<SceneRequest>(text)
            val scene: Scene<Any> = sceneManager[sceneRequest.path]
                ?: throw DroplyException(code = DroplyErrorCode.NOT_FOUND, message = "No such scene")

            val requestContent = sceneRequest.request

            scene.apply {
                val actualRequest = Json.decodeFromString(serializer, requestContent.toString())
                for (droplyMiddleware in middlewareCollection) {
                    // Process before forward middleware scenarios
                    droplyMiddleware.beforeForward(scene, actualRequest, session)
                }

                // Rollout actual scene
                rollout(actualRequest)
            }
        } catch (e: Exception) {
            logger.debug(e) { "Scene error: $e" }
            exceptionProcessor.process(e, session)
        } catch (undeclared: UndeclaredThrowableException) {
            logger.debug(undeclared) { "Undeclared (proxy) error: $undeclared" }
            exceptionProcessor.process(undeclared, session)
        }
    }

    // Send an event once we have finished serving an authorized user
    if (ctx.auth != null) {
        eventPublisher.publishEvent(UserLogoutEvent(ctx.auth!!.user))
    }
}
