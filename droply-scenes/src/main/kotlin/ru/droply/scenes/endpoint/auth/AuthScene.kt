package ru.droply.scenes.endpoint.auth

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.auth.Auth
import ru.droply.service.DroplyUserService
import ru.droply.service.JwtService
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class AuthInDto(val token: String)

@Serializable
data class AuthOutDto(val success: Boolean, val message: String) {
    object Success {
        operator fun invoke(message: String): AuthOutDto {
            return AuthOutDto(true, message)
        }
    }

    object Failure {
        operator fun invoke(message: String): AuthOutDto {
            return AuthOutDto(false, message)
        }
    }
}

typealias Request = AuthInDto
typealias Response = AuthOutDto
typealias Success = AuthOutDto.Success
typealias Failure = AuthOutDto.Failure

@DroplyScene("auth")
class AuthScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userService: DroplyUserService

    override fun DefaultWebSocketSession.handle(request: Request): Response {
        if (ctx.auth != null && userService.fetchUser(ctx) != null) {
            return Failure("You are already logged in")
        }

        val authPayload = jwtService.decodeAuthToken(request.token)
            ?: return Failure("Invalid token")
        val user = userService.findByEmail(authPayload.user.email)
            ?: return Failure("Unknown account")

        ctx.auth = Auth(authPayload.provider, user)

        return Success("Authed in ${ctx.auth!!.user.name}")
    }
}
