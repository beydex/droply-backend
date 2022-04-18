package ru.droply.listener

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.data.entity.DroplyRequest
import ru.droply.dto.update.DroplyUpdateOutDto
import ru.droply.dto.update.DroplyUpdateType
import ru.droply.mapper.DroplyRequestMapper
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.event.UserRequestSendEvent
import ru.droply.sprintor.ktor.sendJson

@Component
class UserRequestSendEventListener {

    @Autowired
    private lateinit var locator: DroplyLocator

    @Autowired
    private lateinit var requestMapper: DroplyRequestMapper

    @EventListener
    fun listenRequestSend(event: UserRequestSendEvent) = runBlocking {
        locator
            .lookupUser(event.request.receiver.id ?: return@runBlocking)
            ?.let { sendUpdate(it, event.request) }
    }

    suspend fun sendUpdate(session: DefaultWebSocketSession, request: DroplyRequest) {
        session.sendJson(
            DroplyUpdateOutDto(
                content = requestMapper.mapDetailed(request),
                type = DroplyUpdateType.REQUEST_RECEIVED
            )
        )
    }
}
