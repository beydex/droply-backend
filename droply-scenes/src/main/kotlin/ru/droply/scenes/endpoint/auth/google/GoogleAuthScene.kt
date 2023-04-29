package ru.droply.scenes.endpoint.auth.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthProvider
import ru.droply.mapper.AuthPayloadMapper
import ru.droply.service.DroplyUserService
import ru.droply.service.JwtService
import ru.droply.service.extensions.auth
import ru.droply.sprintor.event.UserLoginEvent
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class GoogleAuthInDto(val token: String)

@Serializable
data class GoogleAuthOutDto(val success: Boolean, val message: String, val token: String? = null) {
    object Success {
        operator fun invoke(message: String, token: String): GoogleAuthOutDto {
            return GoogleAuthOutDto(true, message, token)
        }
    }

    object Failure {
        operator fun invoke(message: String): GoogleAuthOutDto {
            return GoogleAuthOutDto(false, message)
        }
    }
}

typealias Request = GoogleAuthInDto
typealias Response = GoogleAuthOutDto
typealias Failure = GoogleAuthOutDto.Failure
typealias Success = GoogleAuthOutDto.Success

@DroplyScene("auth/google")
class GoogleAuthScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {
    @Autowired
    private lateinit var tokenVerifier: GoogleIdTokenVerifier

    @Autowired
    private lateinit var userService: DroplyUserService

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var authPayloadMapper: AuthPayloadMapper

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    override fun DefaultWebSocketSession.handle(request: Request, nonce: UUID): Response {
        if (ctx.auth != null && userService.fetchUser(ctx) != null) {
            return Failure("You are already logged in, ${ctx.auth!!.user.name}")
        }

        // Try to parse and validate the token
        val idToken = try {
            tokenVerifier.verify(request.token) ?: return Failure("Invalid token")
        } catch (e: IllegalArgumentException) {
            return Failure("Malformed token")
        }

        val payload = idToken.payload

        // Grab an email
        val email: String = payload.email ?: return Failure("No email found")
        if (payload.emailVerified != true) {
            return Failure("Email is not verified")
        }

        // Check if there is already a user with provided email
        var droplyUser = userService.findByEmail(email)
        if (droplyUser == null) {
            droplyUser = userService.makeUser(
                name = payload.name ?: email.split("@")[0],
                email = email,
                avatarUrl = if ("picture" in payload) payload["picture"] as String else null
            )
        }

        val auth = Auth(AuthProvider.GOOGLE, droplyUser)
        val token = jwtService.issueAuthToken(authPayloadMapper.map(auth))

        ctx.auth = auth
        eventPublisher.publishEvent(UserLoginEvent(droplyUser, this))

        return Success("Welcome to Droply, ${auth.user.name}", token)
    }

    private val GoogleIdToken.Payload.name: String?
        get() {
            return try {
                this["name"] as? String?
            } catch (_: Exception) {
                null
            }
        }
}
