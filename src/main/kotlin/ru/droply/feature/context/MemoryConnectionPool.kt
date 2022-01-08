package ru.droply.feature.context

import io.ktor.http.cio.websocket.*

class MemoryConnectionPool : ConnectionPool {
    private val map: MutableMap<DefaultWebSocketSession, Context> = HashMap()

    override fun get(connection: DefaultWebSocketSession): Context {
        val found = map[connection]
        return if (found == null) {
            val context = MemoryContext()
            map[connection] = context
            context
        } else found

    }

    override fun plusAssign(connection: DefaultWebSocketSession) {
        map[connection] = MemoryContext()
    }
}