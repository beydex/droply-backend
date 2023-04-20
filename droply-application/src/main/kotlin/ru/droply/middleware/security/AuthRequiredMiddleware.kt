package ru.droply.middleware.security

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.data.common.auth.Auth
import ru.droply.middleware.AnnotationMiddleware
import ru.droply.service.DroplyUserService
import ru.droply.service.extensions.auth
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.Scene

@Component
class AuthRequiredMiddleware : AnnotationMiddleware(AuthRequired::class) {
    @Autowired
    private lateinit var userService: DroplyUserService

    override fun <T : Any> handleBeforeForward(
        scene: Scene<T>,
        request: T,
        nonce: UUID,
        session: DefaultWebSocketSession
    ) {
        if (session.ctx.auth == null) {
            throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)
        }
        val user = userService.fetchUser(session.ctx)
            ?: throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)

        session.ctx.auth = Auth(session.ctx.auth!!.provider, user)
    }
}
