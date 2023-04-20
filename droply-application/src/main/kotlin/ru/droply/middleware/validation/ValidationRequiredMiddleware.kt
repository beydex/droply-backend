package ru.droply.middleware.validation

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.middleware.AnnotationMiddleware
import ru.droply.sprintor.middleware.validation.ValidationRequired
import ru.droply.sprintor.scene.Scene
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Component
class ValidationRequiredMiddleware : AnnotationMiddleware(ValidationRequired::class) {
    @Autowired
    private lateinit var validator: Validator

    override fun <T : Any> handleBeforeForward(
        scene: Scene<T>,
        request: T,
        nonce: UUID,
        session: DefaultWebSocketSession
    ) {
        // Do validation
        val violations = validator.validate(request)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }
}
