package ru.droply.sprintor.ktor

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import kotlinx.serialization.encodeToString

suspend inline fun <reified T> DefaultWebSocketSession.sendJson(value: T) {
    send(Frame.Text(json.encodeToString(value)))
}
