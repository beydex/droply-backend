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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import ru.droply.sprintor.ktor.retrieveText
import ru.droply.sprintor.middleware.DroplyMiddleware
import ru.droply.sprintor.processor.ExceptionProcessor
import ru.droply.sprintor.scene.Scene
import ru.droply.sprintor.scene.SceneManager
import ru.droply.sprintor.scene.SceneRequest
import ru.droply.sprintor.spring.autowired
import java.time.Duration

private val sceneManager: SceneManager by autowired()
private val sceneMap: Map<String, Scene<*>> by autowired("sceneMap")

private val middlewareCollection: Collection<DroplyMiddleware> by autowired("middlewareCollection")

private val exceptionProcessor: ExceptionProcessor by autowired()

private val logger = KotlinLogging.logger {}

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
        logger.info { "Scene $path: ${scene::class}" }
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
                ?: throw IllegalStateException("No such scene")

            val requestContent = sceneRequest.request

            scene.apply {
                val actualRequest = Json.decodeFromString(serializer, requestContent.toString())
                middlewareCollection.forEach {
                    // Process before forward middleware scenarios
                    it.beforeForward(scene, actualRequest, session)
                }

                // Rollout actual scene
                rollout(actualRequest)
            }
        } catch (e: Exception) {
            logger.trace(e) { "Scene handler error: $e" }
            exceptionProcessor.process(e, session)
        }
    }
}
