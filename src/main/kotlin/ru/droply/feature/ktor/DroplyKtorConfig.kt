package ru.droply.feature.ktor

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.droply.feature.scene.Scene
import ru.droply.feature.scene.SceneManager
import ru.droply.feature.scene.SceneRequest
import ru.droply.feature.spring.autowired
import ru.droply.scene.auth.GoogleAuthScene
import ru.droply.scene.auth.WhoamiScene
import ru.droply.scene.test.HelloScene
import ru.droply.scene.test.WorldScene
import java.time.Duration

private val sceneManager: SceneManager by autowired()


private val helloScene: HelloScene by autowired()
private val worldScene: WorldScene by autowired()
private val authScene: GoogleAuthScene by autowired()
private val whoamiScene: WhoamiScene by autowired()

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    sceneManager.apply {
        set("hello", helloScene)
        set("world", worldScene)
        set("auth/google", authScene)
        set("whoami", whoamiScene)
    }

    routing {
        webSocket("/") {
            incoming.retrieveText {
                val text = readText()
                try {
                    val sceneRequest = Json.decodeFromString<SceneRequest>(text)
                    val scene: Scene<Any>? = sceneManager[sceneRequest.path]
                    val requestContent = sceneRequest.request

                    // TODO: process middleware

                    scene?.run {
                        rollout(Json.decodeFromString(serializer, requestContent.toString()))
                    } ?: throw IllegalStateException("No such route")

                } catch (e: Exception) {
                    outgoing.sendJson("Something went wrong: " + e.message)
                    e.printStackTrace()
                }
            }
        }
    }
}
