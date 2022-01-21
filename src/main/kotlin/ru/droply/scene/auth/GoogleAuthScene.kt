package ru.droply.scene.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import ru.droply.dao.UserDao
import ru.droply.entity.DroplyUser
import ru.droply.feature.context.auth.Auth
import ru.droply.feature.context.auth.AuthProvider
import ru.droply.feature.ktor.ctx
import ru.droply.feature.scene.variety.RestScene
import ru.droply.feature.spring.autowired

@Serializable
data class GoogleAuthInDto(val token: String)

@Serializable
data class GoogleAuthOutDto(val success: Boolean, val message: String? = null) {
    object Failure {
        operator fun invoke(message: String?): GoogleAuthOutDto {
            return GoogleAuthOutDto(false, message)
        }
    }

    object Success {
        operator fun invoke(message: String?): GoogleAuthOutDto {
            return GoogleAuthOutDto(true, message)
        }
    }
}

typealias Request = GoogleAuthInDto
typealias Response = GoogleAuthOutDto
typealias Failure = GoogleAuthOutDto.Failure
typealias Success = GoogleAuthOutDto.Success

@Component
class GoogleAuthScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {
    private val userRepository: UserDao by autowired()
    private val verifier: GoogleIdTokenVerifier by autowired()

    override fun DefaultWebSocketSession.handle(request: Request): Response {
        if (ctx.auth != null) {
            return Success("You are already logged in, ${ctx.auth!!.user.name}")
        }

        // Try to parse and validate the token
        val idToken = try {
            verifier.verify(request.token) ?: return Failure("Invalid token")
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
        var exist = userRepository.findByEmail(email)
        if (exist == null) {
            // if there is no name, get the part before '@' in email
            val name = payload["name"] as String? ?: email.split("@")[0]
            exist = DroplyUser(name, email).let(userRepository::save)
        }

        ctx.auth = Auth(socialProvider = AuthProvider.GOOGLE, user = exist)
        return Success("Welcome to Droply, ${ctx.auth!!.user.name}")
    }
}