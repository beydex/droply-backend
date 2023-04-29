package ru.droply.sprintor.messaging.messages

open class RequestSignalMessage(val requestId: Long, val senderId: Long, val content: String) : EventMessage()