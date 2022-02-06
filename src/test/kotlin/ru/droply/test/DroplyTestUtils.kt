package ru.droply.test

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

suspend inline fun <reified T> assertReceive(incoming: ReceiveChannel<Frame>): T {
    val response = incoming.receive()
    assert(response is Frame.Text); response as Frame.Text

    return Json.decodeFromString(response.readText())
}
