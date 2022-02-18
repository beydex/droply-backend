package ru.droply.sprintor.processor.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import mu.KotlinLogging
import org.springframework.stereotype.Component
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.sprintor.processor.ExceptionHandlerContainer
import ru.droply.sprintor.processor.exception.DroplyUnauthorizedException

@Component
@ExceptionHandlerContainer
class DroplyUnauthorizedExceptionProcessor {
    private val logger = KotlinLogging.logger {}

    suspend fun process(exception: DroplyUnauthorizedException, session: DefaultWebSocketSession) {
        logger.trace(exception) { "Unauthorized $session" }
        session.outgoing.sendJson(DroplyErrorResponse(code = DroplyErrorCode.UNAUTHORIZED))
    }
}
