package ru.droply.listener

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.data.common.dto.request.RequestSignalDto
import ru.droply.data.entity.DroplyRequest
import ru.droply.dto.update.DroplyUpdateOutDto
import ru.droply.dto.update.DroplyUpdateType
import ru.droply.service.DroplyRequestService
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.event.UserRequestSignalEvent
import ru.droply.sprintor.ktor.sendJson
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException

@Component
class UserRequestSignalEventListener {

    @Autowired
    private lateinit var locator: DroplyLocator

    @Autowired
    private lateinit var requestService: DroplyRequestService

    @EventListener
    fun listenRequestSend(event: UserRequestSignalEvent) {
        if (event.request.id == null) {
            return
        }

        val receiverId = if (event.sender.id == event.request.sender.id) {
            event.request.receiver.id
        } else {
            event.request.sender.id
        }

        if (receiverId == null) {
            return
        }

        val session = locator.lookupUser(receiverId)
        if (session == null) {
            requestService.removeRequest(event.request, false)
            throw DroplyException(
                code = DroplyErrorCode.INTERNAL_ERROR,
                message = "Request receiver is offline"
            )
        }

        runBlocking { sendUpdate(session, event.request, event.content) }
    }

    suspend fun sendUpdate(session: DefaultWebSocketSession, request: DroplyRequest, content: String) {
        session.sendJson(
            DroplyUpdateOutDto(
                content = RequestSignalDto(requestId = request.id!!, content = content),
                type = DroplyUpdateType.REQUEST_SIGNAL
            )
        )
    }
}
