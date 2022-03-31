package ru.droply.scenes.endpoint.code

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.OutRestScene

@Serializable
data class DroplyCodeOutDto(val success: Boolean, val code: Int)

@DroplyScene("code/refresh")
@AuthRequired
class CodeRefreshScene : OutRestScene<DroplyCodeOutDto>(DroplyCodeOutDto.serializer()) {
    @Autowired
    private lateinit var userService: DroplyUserService

    override fun DefaultWebSocketSession.handle(request: Unit) = DroplyCodeOutDto(
        success = true,
        code = userService.updateUserUrid(userService.requireUser(ctx))
    )
}
