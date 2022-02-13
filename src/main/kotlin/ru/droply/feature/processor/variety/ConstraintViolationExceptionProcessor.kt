package ru.droply.feature.processor.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import ru.droply.feature.ktor.sendJson
import ru.droply.feature.processor.DroplyErrorCode
import ru.droply.feature.processor.DroplyErrorResponse
import ru.droply.feature.processor.ExceptionHandlerContainer
import javax.validation.ConstraintViolationException

@Component
@ExceptionHandlerContainer
class ConstraintViolationExceptionProcessor {
    @Serializable
    data class ConstraintViolationInfo(
        val field: String?,
        val message: String
    )

    @Serializable
    data class ConstraintValidationErrorResponse(val error: List<ConstraintViolationInfo>) :
        DroplyErrorResponse(code = DroplyErrorCode.BAD_REQUEST)

    suspend fun process(exception: ConstraintViolationException, session: DefaultWebSocketSession) {
        session.outgoing.sendJson(
            ConstraintValidationErrorResponse(
                exception.constraintViolations.map { violation ->
                    ConstraintViolationInfo(
                        field = violation.propertyPath.toString(),
                        message = violation.message
                    )
                }
            )
        )
    }
}
