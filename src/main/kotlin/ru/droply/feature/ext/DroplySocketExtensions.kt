package ru.droply.feature.ext

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import ru.droply.feature.spring.autowired

val mapper: ObjectMapper by autowired()

object Quit

suspend fun SendChannel<Frame>.sendJson(value: Any) {
    send(Frame.Text(mapper.writeValueAsString(value)))
}

suspend fun ReceiveChannel<Frame>.retrieveText(action: suspend Frame.Text.() -> Any) {
    for (frame in this) {
        if (frame is Frame.Text) {
            if (action(frame) is Quit) {
                return
            }
        }
    }
}

suspend fun ReceiveChannel<Frame>.receiveText(action: suspend Frame.Text.() -> Any) {
    for (frame in this) {
        if (frame is Frame.Text) {
            action(frame)
            return
        }
    }
}

suspend inline fun <reified T : Any> DefaultWebSocketSession.retrieve(): T {
    for (frame in incoming) {
        if (frame is Frame.Text) {
            val text = frame.readText()
            try {
                return mapper.readValue(text, T::class.java)
            } catch (e: Exception) {
                outgoing.sendJson("Malformed request")
                println(e.message)
            }
        }
    }

    throw IllegalStateException("Malformed request")
}

suspend inline fun <reified T : Any> DefaultWebSocketSession.retrieveCyclic(action: (T) -> Any) {
    for (frame in incoming) {
        if (frame is Frame.Text) {
            val text = frame.readText()
            try {
                action(mapper.readValue(text, T::class.java))
            } catch (e: Exception) {
                outgoing.sendJson("Malformed request")
                println(e.message)
            }
        }
    }

    throw IllegalStateException("Malformed request")
}