package ru.droply.scenes.endpoint.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import org.springframework.context.annotation.Profile
import ru.droply.sprintor.context.retDefault
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class WorldResponse(val message: String)

@Profile("test")
@DroplyScene("test/world")
class WorldScene : RestScene<Unit, WorldResponse>(Unit.serializer(), WorldResponse.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Unit) =
        WorldResponse("hi, ${ctx.retDefault("name") { "unknown" }}")
}
