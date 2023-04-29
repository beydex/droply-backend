package ru.droply.sprintor.event

import ru.droply.data.entity.DroplyRequest
import ru.droply.data.entity.DroplyUser

class UserRequestSignalEvent(
    val request: DroplyRequest,
    val sender: DroplyUser,
    val content: String
) : DroplyEvent(request)
