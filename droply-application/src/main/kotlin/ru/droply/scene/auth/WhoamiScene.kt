package ru.droply.scene.auth

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.dto.user.DroplyUserOutDto
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.variety.OutRestScene
import ru.droply.sprintor.spring.autowired

@Serializable
data class WhoamiOutDto(
    val authenticated: Boolean,
    val user: DroplyUserOutDto? = null,
    val provider: AuthProvider? = null
) {
    object NotAuthorized {
        operator fun invoke(): WhoamiOutDto {
            return WhoamiOutDto(false)
        }
    }

    object Authorized {
        private val mapper: DroplyUserMapper by autowired()

        operator fun invoke(auth: Auth): WhoamiOutDto {
            return WhoamiOutDto(
                authenticated = true,
                user = mapper.map(auth.user),
                provider = auth.provider
            )
        }
    }
}

typealias NotAuthorized = WhoamiOutDto.NotAuthorized
typealias Authorized = WhoamiOutDto.Authorized

@Component
class WhoamiScene : OutRestScene<WhoamiOutDto>(WhoamiOutDto.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Unit) =
        if (ctx.auth == null) {
            NotAuthorized()
        } else {
            Authorized(ctx.auth!!)
        }
}
