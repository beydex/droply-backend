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
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.event.UserRequestSignalEvent
import ru.droply.sprintor.ktor.sendJson

@Component
class UserRequestSignalEventListener {

    @Autowired
    private lateinit var locator: DroplyLocator

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
            ?: // Cease to proceed with this request.
            // In reality, we are unsure whether the user is connected to a particular instance or not,
            // so we cannot assume that they are not.
            // Therefore, the request will remain in the database
            // until we add functionality to check whether the user is connected
            // to any instance of the application
            return

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
