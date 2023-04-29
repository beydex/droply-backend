package ru.droply.sprintor.messaging.messages


open class UserRequestAnswerMessage(
    val requestId: Long,
    val issuerId: Long,
    val accept: Boolean,
    val answer: String? = null
) : EventMessage()