package ru.droply.sprintor.scene.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.KSerializer
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.scene.Scene

abstract class RestScene<T : Any, V : Any> constructor(
    override val serializer: KSerializer<T>,
    private val outSerializer: KSerializer<V>
) : Scene<T> {
    abstract fun DefaultWebSocketSession.handle(request: T): V

    override suspend fun DefaultWebSocketSession.rollout(request: T) {
        outgoing.sendJson(handle(request), outSerializer)
    }
}
