package ru.droply.feature.processor.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import mu.KotlinLogging
import org.springframework.stereotype.Component
import ru.droply.feature.ktor.sendJson
import ru.droply.feature.processor.DroplyErrorCode
import ru.droply.feature.processor.DroplyErrorResponse
import ru.droply.feature.processor.ExceptionHandlerContainer

@Component
@ExceptionHandlerContainer
class FallbackExceptionProcessor {
    private val logger = KotlinLogging.logger {}

    suspend fun process(exception: Exception, session: DefaultWebSocketSession) {
        logger.error(exception) { "Internal error: $exception" }
        session.outgoing.sendJson(DroplyErrorResponse(code = DroplyErrorCode.INTERNAL_ERROR))
    }
}
