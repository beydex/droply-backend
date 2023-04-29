package ru.droply.sprintor.messaging.messages

open class RequestSendMessage(val requestId: Long) : EventMessage()