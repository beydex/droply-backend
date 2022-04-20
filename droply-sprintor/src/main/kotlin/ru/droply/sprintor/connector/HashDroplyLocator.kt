package ru.droply.sprintor.connector

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.concurrent.ConcurrentHashMap
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ru.droply.sprintor.event.UserLoginEvent
import ru.droply.sprintor.event.UserLogoutEvent

@Component
class HashDroplyLocator : DroplyLocator {
    private val userSessionMap: MutableMap<Long, DefaultWebSocketSession> = ConcurrentHashMap()

    override fun lookupUser(id: Long) = userSessionMap[id]

    @Order(0)
    @EventListener
    fun onUserAuthorized(userLoginEvent: UserLoginEvent) {
        if (userLoginEvent.user.id == null) {
            return
        }

        val id = userLoginEvent.user.id ?: return
        userSessionMap[id] = userLoginEvent.session
    }

    @Order(0)
    @EventListener
    fun onUserDisconnected(userLogoutEvent: UserLogoutEvent) {
        userSessionMap.remove(userLogoutEvent.user.id)
    }
}
