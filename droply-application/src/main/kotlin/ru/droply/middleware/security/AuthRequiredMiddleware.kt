package ru.droply.middleware.security

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.middleware.AnnotationMiddleware
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.Scene

@Component
class AuthRequiredMiddleware : AnnotationMiddleware(AuthRequired::class) {
    @Autowired
    private lateinit var userService: DroplyUserService

    override fun <T : Any> handleBeforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession) {
        if (session.ctx.auth == null || userService.fetchUser(session.ctx) == null) {
            throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)
        }
    }
}
