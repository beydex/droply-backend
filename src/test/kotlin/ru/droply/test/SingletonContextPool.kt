package ru.droply.test

import io.ktor.http.cio.websocket.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.droply.feature.context.ConnectionPool

@Component
class SingletonContextPool: ConnectionPool {
    @Autowired
    private lateinit var context: TestContext

    override fun get(connection: DefaultWebSocketSession) = context

    override fun toString(): String {
        return "SingletonContextPool(context=$context)"
    }
}