package ru.droply.test

import io.ktor.http.cio.websocket.*
import ru.droply.feature.context.ConnectionPool
import ru.droply.feature.context.Context
import ru.droply.feature.context.MemoryContext

class SingletonConnectionPool: ConnectionPool {
    private val context: Context = MemoryContext()

    override fun get(connection: DefaultWebSocketSession): Context {
        return context
    }

    fun tweak(action: Context.() -> Unit) = action(context)

    override fun plusAssign(connection: DefaultWebSocketSession) {
    }
}