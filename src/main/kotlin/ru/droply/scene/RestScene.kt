package ru.droply.scene

import io.ktor.http.cio.websocket.*
import ru.droply.feature.ext.receiveText
import ru.droply.feature.ext.sendJson
import ru.droply.feature.subrouting.Scene

class RestScene: Scene {
    override suspend fun DefaultWebSocketSession.rollout() {
        incoming.receiveText { outgoing.sendJson(readText()) }
    }
}