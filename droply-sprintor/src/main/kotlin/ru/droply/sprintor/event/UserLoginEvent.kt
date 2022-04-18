package ru.droply.sprintor.event

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import org.springframework.context.ApplicationEvent
import ru.droply.data.entity.DroplyUser

class UserLoginEvent(val user: DroplyUser, val session: DefaultWebSocketSession) : ApplicationEvent(user)
