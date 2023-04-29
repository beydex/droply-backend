package ru.droply.listener

import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ru.droply.etc.toEvent
import ru.droply.etc.toMessage
import ru.droply.sprintor.event.DroplyEvent
import ru.droply.sprintor.event.UserRequestAnswerEvent
import ru.droply.sprintor.event.UserRequestSendEvent
import ru.droply.sprintor.event.UserRequestSignalEvent
import ru.droply.sprintor.event.isExternal
import ru.droply.sprintor.messaging.DroplyEventBus
import ru.droply.sprintor.messaging.messages.EventMessage
import ru.droply.sprintor.messaging.messages.RequestSendMessage
import ru.droply.sprintor.messaging.messages.RequestSignalMessage
import ru.droply.sprintor.messaging.messages.UserRequestAnswerMessage

@Component
class SpringEventBusForwardListener {
    @Autowired
    private lateinit var eventBus: DroplyEventBus

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @PostConstruct
    fun onInit() {
        eventBus.subscribe {
            // These are the only events we care about
            if (!it.isApplicable()) {
                println("$it is UNAPPLICABLE, SKIPPING")
                return@subscribe
            }
            println("Converting $it to EVENT: #${it.toEvent(external = true)}")
            it.toEvent(external = true)?.let { event ->
                // Forward specific received events (if possible)
                // to Spring event publisher
                eventPublisher.publishEvent(event)
            }
        }
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @EventListener
    fun listenAny(event: ApplicationEvent) {
        if (event.isExternal() || event !is DroplyEvent || !event.isApplicable()) {
            return
        }
        // Forward specific locally emitted events (if appropriate)
        // to the event bus
        eventBus.publish(event.toMessage())
    }

    fun ApplicationEvent.isApplicable() =
        this is UserRequestAnswerEvent || this is UserRequestSendEvent || this is UserRequestSignalEvent

    fun EventMessage.isApplicable() =
        this is RequestSendMessage || this is RequestSignalMessage || this is UserRequestAnswerMessage
}
