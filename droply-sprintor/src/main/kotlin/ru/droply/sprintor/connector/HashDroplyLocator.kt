package ru.droply.sprintor.connector

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.sprintor.event.UserAuthorizeEvent
import ru.droply.sprintor.event.UserLogoutEvent

@Component
class HashDroplyLocator : DroplyLocator {
    private val userSessionMap: MutableMap<Long, DefaultWebSocketSession> = mutableMapOf()

    override fun lookupUser(id: Long) = userSessionMap[id]

    @EventListener
    fun onUserAuthorized(userAuthorizeEvent: UserAuthorizeEvent) {
        if (userAuthorizeEvent.user.id in userSessionMap) {
            throw IllegalStateException("User is already in session map (might be double event fire or sync failure)")
        }

        val id = userAuthorizeEvent.user.id ?: return
        userSessionMap[id] = userAuthorizeEvent.session
    }

    @EventListener
    fun onUserDisconnected(userLogoutEvent: UserLogoutEvent) {
        userSessionMap.remove(userLogoutEvent.user.id)
    }
}