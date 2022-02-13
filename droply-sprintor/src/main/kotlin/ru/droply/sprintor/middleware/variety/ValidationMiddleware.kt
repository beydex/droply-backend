package ru.droply.sprintor.middleware.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.sprintor.middleware.DroplyMiddleware
import ru.droply.sprintor.scene.Scene
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

@Component
class ValidationMiddleware : DroplyMiddleware {
    @Autowired
    private lateinit var validator: Validator

    override fun <T : Any> beforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession) {
        val rolloutFunction = scene::class.functions.find { it.name == "rollout" } ?: return

        if (rolloutFunction.parameters.any { it.hasAnnotation<Valid>() }) {
            // Do validation
            val violations = validator.validate(request)
            if (violations.isNotEmpty()) {
                throw ConstraintViolationException(violations)
            }
        }
    }
}
