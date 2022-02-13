package ru.droply.test

import io.ktor.application.Application
import io.ktor.http.cio.websocket.Frame
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import ru.droply.configureSockets
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.scene.SceneRequest

fun socket(
    request: SceneRequest,
    handle: suspend TestApplicationCall.(incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) -> Unit = { _, _ -> }
) = withTestApplication(Application::configureSockets) {
    handleWebSocketConversation(
        "/",
        callback = { incoming, outgoing ->
            outgoing.sendJson(request)
            handle(incoming, outgoing)
        }
    )
}

fun socketIncoming(
    request: SceneRequest,
    setup: TestApplicationRequest.() -> Unit = {},
    handle: suspend TestApplicationCall.(incoming: ReceiveChannel<Frame>) -> Unit = {}
) = withTestApplication(Application::configureSockets) {
    handleWebSocketConversation(
        "/",
        setup = setup,
        callback = { incoming, outgoing ->
            outgoing.sendJson(request)
            handle(incoming)
        }
    )
}

fun makeRequest(path: String, vararg entries: Pair<String, JsonElement>): SceneRequest =
    SceneRequest(path, JsonObject(mapOf(*entries)))

inline fun <reified T> makeRequest(path: String, data: T): SceneRequest =
    SceneRequest(path, Json.encodeToJsonElement(data).jsonObject)
