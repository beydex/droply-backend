package ru.droply.sprintor.event

import ru.droply.data.entity.DroplyRequest

class UserRequestSendEvent(val request: DroplyRequest) : DroplyEvent(request)
