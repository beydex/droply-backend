package ru.droply.sprintor.messaging.messages

import java.io.Serializable
import java.util.UUID
import ru.droply.sprintor.messaging.RedissonEventBus

abstract class EventMessage : Serializable {
    val nonce: UUID = RedissonEventBus.NONCE
}