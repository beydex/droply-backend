package ru.droply.feature.context

import io.ktor.http.cio.websocket.*

interface ConnectionPool {
    operator fun get(connection: DefaultWebSocketSession): Context
}
