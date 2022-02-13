package ru.droply.sprintor.processor

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlin.reflect.KClass

interface ExceptionProcessor {
    fun process(exception: Exception, session: DefaultWebSocketSession)

    fun <T : Exception> append(exceptionClass: KClass<T>, handler: (T, DefaultWebSocketSession) -> Unit)
}
