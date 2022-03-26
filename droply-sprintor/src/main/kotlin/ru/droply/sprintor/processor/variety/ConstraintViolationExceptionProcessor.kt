package ru.droply.sprintor.processor.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import javax.validation.ConstraintViolationException
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.sprintor.processor.ExceptionHandlerContainer

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
