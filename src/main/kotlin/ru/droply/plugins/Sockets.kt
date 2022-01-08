package ru.droply.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import ru.droply.auth.authRouter
import ru.droply.feature.ext.Quit
import ru.droply.feature.ext.retrieveCyclic
import ru.droply.feature.spring.autowired
import ru.droply.feature.subrouting.SceneManager
import ru.droply.feature.subrouting.SceneRequest
import ru.droply.scene.RestScene
import ru.droply.scene.SecondTestScene
import ru.droply.scene.TestScene
import java.time.Duration

private val sceneManager: SceneManager by autowired()

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    sceneManager["test"] = TestScene()
    sceneManager["test2"] = SecondTestScene()
    sceneManager["rest"] = RestScene()

    routing {
        webSocket("/") {
            authRouter()
            retrieveCyclic<SceneRequest> {
                sceneManager[it.path]?.run {
                    when (rollout()) {
                        is Quit -> outgoing.send(Frame.Close())
                    }
                } ?: throw IllegalStateException("No such route")
            }
        }
    }
}
