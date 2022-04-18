package ru.droply.sprintor.connector

import io.ktor.http.cio.websocket.DefaultWebSocketSession

interface DroplyLocator {
    fun lookupUser(id: Long): DefaultWebSocketSession?
}
