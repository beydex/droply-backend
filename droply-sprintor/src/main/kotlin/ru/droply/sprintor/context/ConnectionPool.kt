package ru.droply.sprintor.context

import io.ktor.http.cio.websocket.DefaultWebSocketSession

interface ConnectionPool {
    operator fun get(connection: DefaultWebSocketSession): Context
}
