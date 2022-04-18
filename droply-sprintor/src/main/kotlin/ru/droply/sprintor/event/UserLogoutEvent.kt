package ru.droply.sprintor.event

import org.springframework.context.ApplicationEvent
import ru.droply.data.entity.DroplyUser

class UserLogoutEvent(val user: DroplyUser) : ApplicationEvent(user)
