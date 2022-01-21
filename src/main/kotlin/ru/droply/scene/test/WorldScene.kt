package ru.droply.scene.test

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import org.springframework.stereotype.Component
import ru.droply.feature.context.retDefault
import ru.droply.feature.ktor.ctx
import ru.droply.feature.scene.variety.RestScene

@Serializable
data class WorldResponse(val message: String)

@Component
class WorldScene : RestScene<Unit, WorldResponse>(Unit.serializer(), WorldResponse.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Unit): WorldResponse =
        WorldResponse("hi, ${ctx.retDefault("name") { "unknown" }}")
}