package ru.droply.scene

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.http.cio.websocket.*
import ru.droply.feature.ext.retrieveCyclic
import ru.droply.feature.ext.sendJson
import ru.droply.feature.subrouting.Scene

class TestScene : Scene {
    data class Response @JsonCreator constructor(@JsonProperty("message") val message: String)

    override suspend fun DefaultWebSocketSession.rollout() {
        retrieveCyclic<Response> {
            outgoing.sendJson(it.message)
        }
    }
}