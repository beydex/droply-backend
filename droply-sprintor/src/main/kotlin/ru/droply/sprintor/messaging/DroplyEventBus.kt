package ru.droply.sprintor.messaging

import ru.droply.sprintor.messaging.messages.EventMessage
import kotlin.reflect.KClass

interface DroplyEventBus {
    /**
     * Publish new event
     *
     * @param message event message to publish
     */
    fun <T : EventMessage> publish(message: T)

    /**
     * Subscribe to all the events
     *
     * @param callback callback to handle the event
     */
    fun subscribe(callback: (EventMessage) -> Unit)
}