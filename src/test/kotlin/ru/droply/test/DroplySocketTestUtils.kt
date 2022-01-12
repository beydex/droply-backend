package ru.droply.test

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.json.*
import ru.droply.feature.ktor.configureSockets
import ru.droply.feature.ktor.sendJson
import ru.droply.feature.scene.SceneRequest

fun socket(
    request: SceneRequest,
    handle: suspend TestApplicationCall.(incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) -> Unit = { _, _ -> }
) = withTestApplication(Application::configureSockets) {
    handleWebSocketConversation("/",
        callback = { incoming, outgoing ->
            outgoing.sendJson(request)
            handle(incoming, outgoing)
        })
}

fun socketIncoming(
    request: SceneRequest,
    handle: suspend TestApplicationCall.(incoming: ReceiveChannel<Frame>) -> Unit = {}
) = withTestApplication(Application::configureSockets) {
    handleWebSocketConversation("/",
        callback = { incoming, outgoing ->
            outgoing.sendJson(request)
            handle(incoming)
        })
}

fun makeRequest(path: String, vararg entries: Pair<String, JsonElement>): SceneRequest =
    SceneRequest(path, JsonObject(mapOf(*entries)))

inline fun <reified T> makeRequest(path: String, data: T): SceneRequest =
    SceneRequest(path, Json.encodeToJsonElement(data).jsonObject)