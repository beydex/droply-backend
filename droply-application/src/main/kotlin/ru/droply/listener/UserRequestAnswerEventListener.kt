package ru.droply.listener

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.data.common.dto.request.RequestAnswerDto
import ru.droply.dto.update.DroplyUpdateOutDto
import ru.droply.dto.update.DroplyUpdateType
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.event.UserRequestAnswerEvent
import ru.droply.sprintor.ktor.sendJson

@Component
class UserRequestAnswerEventListener {

    @Autowired
    private lateinit var locator: DroplyLocator

    @EventListener
    fun listenRequestSend(event: UserRequestAnswerEvent) {
        val requestId = event.request.id ?: return
        val endUserId =
            if (event.issuer.id == event.request.sender.id) {
                event.request.receiver.id
            } else {
                event.request.sender.id
            }

        if (endUserId == null) {
            return
        }

        val session = locator.lookupUser(endUserId) ?: return

        runBlocking {
            sendUpdate(session, requestId, event.accept, event.answer)
        }
    }

    suspend fun sendUpdate(session: DefaultWebSocketSession, requestId: Long, accept: Boolean, answer: String?) {
        session.sendJson(
            DroplyUpdateOutDto(
                content = RequestAnswerDto(requestId, accept, answer),
                type = DroplyUpdateType.REQUEST_ANSWERED
            )
        )
    }
}
