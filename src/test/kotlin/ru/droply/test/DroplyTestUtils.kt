package ru.droply.test

import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.droply.feature.context.Context

suspend inline fun <reified T> TestApplicationCall.assertReceive(incoming: ReceiveChannel<Frame>): T {
    val response = incoming.receive()
    assert(response is Frame.Text); response as Frame.Text

    return Json.decodeFromString(response.readText())
}

data class MockedContextRuntime(val context: Context) {
    operator fun invoke(action: () -> Unit) {
        action()
    }
}