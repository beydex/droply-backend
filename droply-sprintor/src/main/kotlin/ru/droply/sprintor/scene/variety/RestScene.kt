package ru.droply.sprintor.scene.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import ru.droply.data.serializer.UUIDSerializer
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.scene.Scene

abstract class RestScene<T : Any, V : Any> constructor(
    override val serializer: KSerializer<T>,
    private val outSerializer: KSerializer<V>
) : Scene<T> {
    abstract fun DefaultWebSocketSession.handle(request: T, nonce: UUID): V

    override suspend fun DefaultWebSocketSession.rollout(request: T, nonce: UUID) {
        outgoing.sendJson(
            Json.encodeToJsonElement(outSerializer, handle(request, nonce))
                .jsonObject
                .plus("nonce" to Json.encodeToJsonElement(UUIDSerializer, nonce))
        )
    }
}
