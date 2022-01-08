package ru.droply.feature.subrouting

import io.ktor.http.cio.websocket.*

interface Scene {
    suspend fun DefaultWebSocketSession.rollout(): Any
}