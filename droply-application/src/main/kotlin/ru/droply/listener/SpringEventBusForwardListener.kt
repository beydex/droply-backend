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
import ru.droply.sprintor.event.isExternal
import ru.droply.sprintor.messaging.DroplyEventBus

@Component
class SpringEventBusForwardListener {
    @Autowired
    private lateinit var eventBus: DroplyEventBus

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @PostConstruct
    fun onInit() {
        eventBus.subscribe {
            it.toEvent(external = true)?.let { event ->
                // Forward all the received events (if possible)
                // to Spring event publisher
                eventPublisher.publishEvent(event)
            }
        }
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @EventListener
    fun listenAny(event: ApplicationEvent) {
        if (event.isExternal() || event !is DroplyEvent) {
            return
        }
        // Forward all the locally emitted events (if appropriate)
        // to the event bus
        eventBus.publish(event.toMessage())
    }
}
