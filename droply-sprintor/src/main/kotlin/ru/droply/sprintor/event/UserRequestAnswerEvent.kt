package ru.droply.sprintor.event

import org.springframework.context.ApplicationEvent
import ru.droply.data.entity.DroplyRequest

class UserRequestAnswerEvent(val request: DroplyRequest, val accept: Boolean, val answer: String? = null) :
    ApplicationEvent(request)
