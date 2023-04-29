package ru.droply.sprintor.messaging.messages

open class UserLoginMessage(val userId: Long) : EventMessage()