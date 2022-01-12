package ru.droply.scene

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import ru.droply.feature.context.retDefault
import ru.droply.feature.ktor.ctx
import ru.droply.feature.scene.RestScene

@Serializable
data class WorldResponse(val message: String)

class WorldScene : RestScene<Unit, WorldResponse>(Unit.serializer(), WorldResponse.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Unit): WorldResponse =
        WorldResponse("hi, ${ctx.retDefault("name") { "unknown" }}")
}