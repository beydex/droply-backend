package ru.droply.sprintor.event

import org.springframework.context.ApplicationEvent
import ru.droply.data.entity.DroplyRequest

class UserRequestSendEvent(val request: DroplyRequest) : ApplicationEvent(request)
