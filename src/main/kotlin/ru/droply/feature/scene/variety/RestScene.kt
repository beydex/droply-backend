package ru.droply.feature.scene.variety

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.KSerializer
import ru.droply.feature.ktor.sendJson
import ru.droply.feature.scene.Scene

abstract class RestScene<T : Any, V : Any> constructor(
    override val serializer: KSerializer<T>,
    private val outSerializer: KSerializer<V>
) : Scene<T> {
    abstract fun DefaultWebSocketSession.handle(request: T): V

    override suspend fun DefaultWebSocketSession.rollout(request: T) {
        outgoing.sendJson(handle(request), outSerializer)
    }
}