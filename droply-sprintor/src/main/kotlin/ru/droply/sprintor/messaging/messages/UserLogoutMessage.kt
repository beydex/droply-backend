package ru.droply.sprintor.messaging.messages

open class UserLogoutMessage(val userId: Long) : EventMessage()