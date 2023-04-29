package ru.droply.sprintor.event

import ru.droply.data.entity.DroplyRequest
import ru.droply.data.entity.DroplyUser

class UserRequestAnswerEvent(
    val request: DroplyRequest,
    val issuer: DroplyUser,
    val accept: Boolean,
    val answer: String? = null
) : DroplyEvent(request)
