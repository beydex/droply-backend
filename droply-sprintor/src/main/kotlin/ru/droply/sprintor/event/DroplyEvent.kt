package ru.droply.sprintor.event

import java.util.UUID
import org.springframework.context.ApplicationEvent

abstract class DroplyEvent(source: Any = UUID.randomUUID()) : ApplicationEvent(source) {

    fun makeExternal() {
        this.source = ExternalEventSource
    }

    object ExternalEventSource
}

fun ApplicationEvent.isExternal() = source == DroplyEvent.ExternalEventSource