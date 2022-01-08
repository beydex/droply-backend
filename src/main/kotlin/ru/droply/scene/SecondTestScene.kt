package ru.droply.scene

import io.ktor.http.cio.websocket.*
import ru.droply.feature.ext.Quit
import ru.droply.feature.ext.retrieveText
import ru.droply.feature.ext.sendJson
import ru.droply.feature.subrouting.Scene

class SecondTestScene : Scene {
    override suspend fun DefaultWebSocketSession.rollout() {
        data class Response(val message: String)

        incoming.retrieveText {
            val text = readText()
            if (text == "quit") {
                return@retrieveText Quit
            }

            outgoing.sendJson(Response(message = "[Test2] You just have said: $text"))
        }
    }
}