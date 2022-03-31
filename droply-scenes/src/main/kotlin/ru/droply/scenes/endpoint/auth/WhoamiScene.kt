package ru.droply.scenes.endpoint.auth

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.common.dto.DroplyUserOutDto
import ru.droply.data.entity.DroplyUser
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.OutRestScene
import ru.droply.sprintor.spring.autowired

@Serializable
data class WhoamiOutDto(
    val success: Boolean,
    val user: DroplyUserOutDto? = null,
    val provider: AuthProvider? = null
) {
    object Authorized {
        private val mapper: DroplyUserMapper by autowired()

        operator fun invoke(auth: Auth, user: DroplyUser): WhoamiOutDto {
            return WhoamiOutDto(
                success = true,
                user = mapper.map(user),
                provider = auth.provider
            )
        }
    }
}

@DroplyScene("auth/whoami")
@AuthRequired
class WhoamiScene : OutRestScene<WhoamiOutDto>(WhoamiOutDto.serializer()) {
    @Autowired
    private lateinit var userService: DroplyUserService

    override fun DefaultWebSocketSession.handle(request: Unit): WhoamiOutDto {
        return WhoamiOutDto.Authorized(
            auth = ctx.auth!!,
            user = userService.requireUser(ctx)
        )
    }
}
