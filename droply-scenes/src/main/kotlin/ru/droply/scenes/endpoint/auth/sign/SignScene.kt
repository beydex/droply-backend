package ru.droply.scenes.endpoint.auth.sign

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.security.MessageDigest
import javax.validation.constraints.Email
import javax.validation.constraints.Size
import kotlinx.serialization.Serializable
import org.bouncycastle.util.encoders.Hex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.entity.DroplyUserConstraints
import ru.droply.mapper.AuthPayloadMapper
import ru.droply.service.DroplyUserService
import ru.droply.service.JwtService
import ru.droply.service.extensions.auth
import ru.droply.sprintor.event.UserLoginEvent
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.validation.ValidationRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene


@Serializable
data class SignSceneInDto(
    @Email val email: String,
    @Size(
        min = DroplyUserConstraints.MIN_PASSWORD_LENGTH,
        max = DroplyUserConstraints.MAX_PASSWORD_LENGTH
    ) val password: String
)

@Serializable
data class SignSceneOutDto(val success: Boolean, val message: String, val token: String? = null) {
    object Success {
        operator fun invoke(message: String, token: String) = SignSceneOutDto(true, message, token)
    }

    object Failure {
        operator fun invoke(message: String) = SignSceneOutDto(false, message)
    }
}

typealias Request = SignSceneInDto
typealias Response = SignSceneOutDto
typealias Success = SignSceneOutDto.Success
typealias Failure = SignSceneOutDto.Failure

@DroplyScene("auth/sign")
@ValidationRequired
class SignScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {

    @Autowired
    private lateinit var userService: DroplyUserService

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var authPayloadMapper: AuthPayloadMapper


    override fun DefaultWebSocketSession.handle(request: Request): Response {
        if (ctx.auth != null && userService.fetchUser(ctx) != null) {
            return Failure("You are already logged in")
        }

        var droplyUser = userService.findByEmail(request.email)
        if (droplyUser != null && droplyUser.passwordHash == null) {
            // In case user is registered via Google
            return Failure("Invalid credentials")
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val passwordHash = String(Hex.encode(digest.digest(request.password.encodeToByteArray())))

        if (droplyUser == null) {
            droplyUser = userService.makeUser(
                name = request.email.split("@")[0],
                email = request.email,
                passwordHash = passwordHash
            )
        } else if (droplyUser.passwordHash != passwordHash) {
            return Failure("Invalid credentials")
        }

        eventPublisher.publishEvent(UserLoginEvent(droplyUser, this))
        val auth = Auth(AuthProvider.SELF, droplyUser)
        val token = jwtService.issueAuthToken(authPayloadMapper.map(auth))
        ctx.auth = auth

        return Success("Authed in ${auth.user.name}", token)
    }
}
