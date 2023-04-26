package ru.droply.sprintor.messaging

import java.util.UUID
import org.redisson.api.RTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.sprintor.messaging.messages.EventMessage
import kotlin.reflect.KClass

@Component
class RedissonEventBus : DroplyEventBus {
    @Autowired
    private lateinit var topic: RTopic

    companion object {
        // A random final UUID to make sure we ignore
        // our own messages
        val NONCE: UUID = UUID.randomUUID()
    }

    override fun <T : EventMessage> publish(message: T) {
        topic.publish(message)
    }

    override fun <T : EventMessage> subscribe(clazz: KClass<T>, callback: (T) -> Unit) {
        topic.addListener(clazz.java) { _, message ->
            if (message.nonce == NONCE) {
                return@addListener
            }
            callback(message)
        }
    }

    override fun subscribe(callback: (EventMessage) -> Unit) {
        topic.addListener(EventMessage::class.java) { _, message ->
            if (message.nonce == NONCE) {
                return@addListener
            }
            callback(message)
        }
    }
}