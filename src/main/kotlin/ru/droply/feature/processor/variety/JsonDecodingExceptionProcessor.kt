package ru.droply.feature.processor.variety

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.SerializationException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import ru.droply.feature.ktor.sendJson
import ru.droply.feature.processor.DroplyErrorCode
import ru.droply.feature.processor.DroplyErrorResponse
import ru.droply.feature.processor.ExceptionHandlerContainer

@Component
@ExceptionHandlerContainer
class JsonDecodingExceptionProcessor {
    private val logger = KotlinLogging.logger {}

    suspend fun process(exception: SerializationException, session: DefaultWebSocketSession) {
        logger.trace(exception) { "Serialization failure: $exception" }
        session.outgoing.sendJson(DroplyErrorResponse(code = DroplyErrorCode.MALFORMED_REQUEST))
    }
}
