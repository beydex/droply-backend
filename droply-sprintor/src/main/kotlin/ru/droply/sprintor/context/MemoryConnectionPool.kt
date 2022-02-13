package ru.droply.sprintor.context

import io.ktor.http.cio.websocket.DefaultWebSocketSession

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
}
