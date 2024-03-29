package ru.droply.listener

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.service.DroplyRequestService
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.event.UserLogoutEvent
import ru.droply.sprintor.event.isExternal

@Component
class UserLogoutCancelRequestsEventListener {

    @Autowired
    private lateinit var requestService: DroplyRequestService

    @Autowired
    private lateinit var userService: DroplyUserService

    @EventListener
    fun listenRequestSend(event: UserLogoutEvent) {
        if (event.isExternal()) {
            // The instance emitting the event
            // should handle request removal on its own
            return
        }

        val userId = event.user.id ?: return
        val user = userService.findFetchOutgoingRequests(userId) ?: return

        for (outgoingRequest in user.outgoingRequests) {
            requestService.removeRequest(droplyRequest = outgoingRequest, issuer = user, accept = false)
        }
    }
}
