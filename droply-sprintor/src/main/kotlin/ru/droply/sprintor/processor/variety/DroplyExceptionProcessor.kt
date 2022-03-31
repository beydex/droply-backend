package ru.droply.sprintor.processor.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import mu.KotlinLogging
import org.springframework.stereotype.Component
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.sprintor.processor.ExceptionHandlerContainer
import ru.droply.sprintor.processor.exception.DroplyException

@Component
@ExceptionHandlerContainer
class DroplyExceptionProcessor {
    private val logger = KotlinLogging.logger {}

    suspend fun process(exception: DroplyException, session: DefaultWebSocketSession) {
        if (exception.message != null) {
            logger.trace(exception) { "Exception message: ${exception.message}}" }
        }
        session.outgoing.sendJson(DroplyErrorResponse(code = exception.code))
    }
}
