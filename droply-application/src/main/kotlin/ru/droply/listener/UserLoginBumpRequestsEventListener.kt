package ru.droply.listener

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ru.droply.data.entity.DroplyRequest
import ru.droply.dto.update.DroplyUpdateOutDto
import ru.droply.dto.update.DroplyUpdateType
import ru.droply.mapper.DroplyRequestMapper
import ru.droply.service.DroplyRequestService
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.event.UserLoginEvent
import ru.droply.sprintor.event.isExternal
import ru.droply.sprintor.ktor.sendJson

@Component
class UserLoginBumpRequestsEventListener {

    @Autowired
    private lateinit var locator: DroplyLocator

    @Autowired
    private lateinit var userService: DroplyUserService

    @Autowired
    private lateinit var requestService: DroplyRequestService

    @Autowired
    private lateinit var requestMapper: DroplyRequestMapper

    @Order(2)
    @EventListener
    fun listenRequestSend(event: UserLoginEvent) {
        if (event.isExternal()) {
            // Instance emitting the event handles
            // all the notifications about incoming requests
            // on its own
            return
        }

        val userId = event.user.id ?: return
        val user = userService.findFetchIncomingRequests(userId) ?: return

        runBlocking {
            user.incomingRequests.forEach { incomingRequest ->
                val request = requestService.fetchRequest(incomingRequest.id!!) ?: return@forEach
                if (request.active) {
                    return@forEach
                }

                locator
                    .lookupUser(userId)
                    ?.let { sendUpdate(it, request) }
            }
        }
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
