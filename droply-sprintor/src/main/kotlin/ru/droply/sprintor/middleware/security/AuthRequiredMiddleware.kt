package ru.droply.sprintor.middleware.security

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import org.springframework.stereotype.Component
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.AnnotationMiddleware
import ru.droply.sprintor.processor.exception.DroplyUnauthorizedException
import ru.droply.sprintor.scene.Scene

@Component
class AuthRequiredMiddleware : AnnotationMiddleware(AuthRequired::class) {
    override fun <T : Any> handleBeforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession) {
        if (session.ctx.auth == null) {
            throw DroplyUnauthorizedException()
        }
    }
}
