package ru.droply.sprintor.ktor

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.droply.sprintor.context.ConnectionPool
import ru.droply.sprintor.context.Context
import ru.droply.sprintor.spring.autowired

object Quit

val json = Json { encodeDefaults = true }

suspend inline fun <reified T> SendChannel<Frame>.sendJson(value: T) {
    send(Frame.Text(json.encodeToString(value)))
}

suspend inline fun <T : Any> SendChannel<Frame>.sendJson(value: T, serializer: KSerializer<Any>) {
    send(Frame.Text(json.encodeToString(serializer, value)))
}

suspend fun ReceiveChannel<Frame>.retrieveText(action: suspend Frame.Text.() -> Any?) {
    for (frame in this) {
        if (frame is Frame.Text) {
            if (action(frame) is Quit) {
                return
            }
        }
    }
}

var connectionPool: ConnectionPool by autowired()

val DefaultWebSocketSession.ctx: Context
    get() = connectionPool[this]
