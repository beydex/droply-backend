package ru.droply.sprintor.event

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import ru.droply.data.entity.DroplyUser

class UserLoginEvent(val user: DroplyUser, val session: DefaultWebSocketSession) : DroplyEvent(user)
