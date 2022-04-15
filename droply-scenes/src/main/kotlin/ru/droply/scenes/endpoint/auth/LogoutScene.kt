package ru.droply.scenes.endpoint.auth

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import ru.droply.sprintor.event.UserLogoutEvent
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.OutRestScene

@Serializable
data class LogoutSceneOutDto(val success: Boolean)

@DroplyScene("auth/logout")
@AuthRequired
class LogoutScene : OutRestScene<LogoutSceneOutDto>(LogoutSceneOutDto.serializer()) {
    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    override fun DefaultWebSocketSession.handle(request: Unit): LogoutSceneOutDto {
        try {
            eventPublisher.publishEvent(UserLogoutEvent(ctx.auth!!.user))
        } finally {
            ctx.auth = null
        }

        return LogoutSceneOutDto(true)
    }
}