package ru.droply.scenes.endpoint.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Profile
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class HelloInDto(val name: String)

@Serializable
data class HelloOutDto(val message: String)

@Profile("test")
@DroplyScene("test/hello")
class HelloScene : RestScene<HelloInDto, HelloOutDto>(HelloInDto.serializer(), HelloOutDto.serializer()) {
    override fun DefaultWebSocketSession.handle(request: HelloInDto): HelloOutDto {
        ctx["name"] = request.name
        return HelloOutDto("Welcome to Droply, ${request.name}")
    }
}
