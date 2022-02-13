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
import ru.droply.scene.auth.AuthScene
import ru.droply.scene.auth.WhoamiScene
import ru.droply.scene.auth.google.GoogleAuthScene
import ru.droply.scene.test.HelloScene
import ru.droply.scene.test.ValidationScene
import ru.droply.scene.test.WorldScene
import ru.droply.sprintor.ktor.retrieveText
import ru.droply.sprintor.middleware.DroplyMiddleware
import ru.droply.sprintor.processor.ExceptionProcessor
import ru.droply.sprintor.scene.Scene
import ru.droply.sprintor.scene.SceneManager
import ru.droply.sprintor.scene.SceneRequest
import ru.droply.sprintor.spring.autowired
import java.time.Duration

private val sceneManager: SceneManager by autowired()
private val middlewareList: List<DroplyMiddleware> by autowired()
private val exceptionProcessor: ExceptionProcessor by autowired()
private val logger = KotlinLogging.logger {}

object Scenes {
    object Test {
        internal val hello: HelloScene by autowired()
        internal val world: WorldScene by autowired()
        internal val valid: ValidationScene by autowired()
    }

    object Auth {
        internal val whoami: WhoamiScene by autowired()
        internal val google: GoogleAuthScene by autowired()
        internal val auth: AuthScene by autowired()
    }
}

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    setupScenes()

    routing {
        webSocket("/") { serveDroplyWebsocket() }
    }
}

private fun setupScenes() {
    sceneManager.apply {
        with(Scenes.Test) {
            set("hello", hello)
            set("world", world)
            set("valid", valid)
        }

        with(Scenes.Auth) {
            set("auth/whoami", whoami)
            set("auth/google", google)
            set("auth", auth)
        }
    }
}

private suspend fun DefaultWebSocketSession.serveDroplyWebsocket() {
    val session = this
    incoming.retrieveText {
        val text = readText()
        try {
            val sceneRequest = Json.decodeFromString<SceneRequest>(text)
            val scene: Scene<Any>? = sceneManager[sceneRequest.path]
            val requestContent = sceneRequest.request

            if (scene == null) {
                throw IllegalStateException("No such route")
            }

            scene.apply {
                val actualRequest = Json.decodeFromString(serializer, requestContent.toString())
                middlewareList.forEach {
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
