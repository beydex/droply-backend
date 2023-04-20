package ru.droply.scenes.endpoint.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
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
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Serializable
data class LogMeInSceneInDto(val email: String)

@Serializable
data class LogMeInSceneOutDto(val success: Boolean, val userId: Long, val token: String)

typealias Request = LogMeInSceneInDto
typealias Response = LogMeInSceneOutDto

@Profile("test-stand")
@DroplyScene("test/logmein")
class LogMeInScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {
    @Autowired
    private lateinit var userService: DroplyUserService

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var authMapper: AuthPayloadMapper

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    override fun DefaultWebSocketSession.handle(request: Request, nonce: UUID): Response {
        val user = userService.findByEmail(request.email) ?: userService.makeUser(
            name = request.email.split("@")[0],
            email = request.email
        )

        val auth = Auth(provider = AuthProvider.CUSTOM, user = user)
            .also { ctx.auth = it }

        val token = jwtService.issueAuthToken(
            authPayload = authMapper.map(auth),
            expiresAt = Instant.now().plus(1, ChronoUnit.HOURS)
        )

        eventPublisher.publishEvent(UserLoginEvent(user, this))

        return Response(
            success = true,
            userId = user.id!!,
            token = token
        )
    }
}
