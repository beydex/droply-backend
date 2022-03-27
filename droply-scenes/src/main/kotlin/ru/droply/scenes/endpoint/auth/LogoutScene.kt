package ru.droply.scenes.endpoint.auth

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.OutRestScene

@kotlinx.serialization.Serializable
data class LogoutSceneOutDto(val success: Boolean)

@DroplyScene("auth/logout")
@AuthRequired
class LogoutScene : OutRestScene<LogoutSceneOutDto>(LogoutSceneOutDto.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Unit): LogoutSceneOutDto {
        ctx.auth = null
        return LogoutSceneOutDto(true)
    }
}