package ru.droply.scene.auth

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import ru.droply.dto.user.DroplyUserOutDto
import ru.droply.feature.context.auth.Auth
import ru.droply.feature.context.auth.AuthProvider
import ru.droply.feature.ktor.ctx
import ru.droply.feature.scene.variety.OutRestScene
import ru.droply.mapper.DroplyUserMapper

private val droplyUserMapper: DroplyUserMapper = Mappers.getMapper(DroplyUserMapper::class.java)

@Serializable
data class WhoamiResponse(
    val authenticated: Boolean,
    val user: DroplyUserOutDto? = null,
    val provider: AuthProvider? = null
) {
    object NotAuthorized {
        operator fun invoke(): WhoamiResponse {
            return WhoamiResponse(false)
        }
    }

    object Authorized {
        operator fun invoke(auth: Auth): WhoamiResponse {
            return WhoamiResponse(true, droplyUserMapper.map(auth.user), auth.socialProvider)
        }
    }
}

typealias NotAuthorized = WhoamiResponse.NotAuthorized
typealias Authorized = WhoamiResponse.Authorized

@Component
class WhoamiScene : OutRestScene<WhoamiResponse>(WhoamiResponse.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Unit) =
        if (ctx.auth == null) {
            NotAuthorized()
        } else {
            Authorized(ctx.auth!!)
        }
}