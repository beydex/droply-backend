package ru.droply.scene.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class HelloInDto(val name: String)

@Serializable
data class HelloOutDto(val message: String)

@Component
class HelloScene : RestScene<HelloInDto, HelloOutDto>(HelloInDto.serializer(), HelloOutDto.serializer()) {
    override fun DefaultWebSocketSession.handle(request: HelloInDto): HelloOutDto {
        ctx["name"] = request.name
        return HelloOutDto("Welcome to Droply, ${request.name}")
    }
}
