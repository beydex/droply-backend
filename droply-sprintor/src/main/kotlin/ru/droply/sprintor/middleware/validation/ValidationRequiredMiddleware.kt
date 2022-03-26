package ru.droply.sprintor.middleware.validation

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import javax.validation.ConstraintViolationException
import javax.validation.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.sprintor.middleware.AnnotationMiddleware
import ru.droply.sprintor.scene.Scene

@Component
class ValidationRequiredMiddleware : AnnotationMiddleware(ValidationRequired::class) {
    @Autowired
    private lateinit var validator: Validator

    override fun <T : Any> handleBeforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession) {
        // Do validation
        val violations = validator.validate(request)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }
}
