package ru.droply.sprintor.scene

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.KSerializer

interface Scene<T : Any> {
    val serializer: KSerializer<T>
    suspend fun DefaultWebSocketSession.rollout(request: T): Any
}
