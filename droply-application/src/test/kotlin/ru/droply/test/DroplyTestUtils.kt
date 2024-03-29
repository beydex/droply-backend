package ru.droply.test

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }

suspend inline fun <reified T> assertReceive(incoming: ReceiveChannel<Frame>): T {
    val response = incoming.receive()
    assert(response is Frame.Text); response as Frame.Text

    return json.decodeFromString(response.readText())
}
