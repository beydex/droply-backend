package ru.droply.sprintor.processor

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

class HashAndHierarchyExceptionProcessor : ExceptionProcessor {
    private val map: MutableMap<KClass<out Exception>, (Exception, DefaultWebSocketSession) -> Unit> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun process(exception: Exception, session: DefaultWebSocketSession) {
        var currentNode = exception::class
        do {
            val processor = map[currentNode]
            if (processor != null) {
                processor.invoke(exception, session)
                break
            }

            currentNode = currentNode.superclasses.find { it.isSubclassOf(Exception::class) }
                    as? KClass<out Exception>
                ?: break
        } while (currentNode.superclasses.isNotEmpty())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Exception> append(exceptionClass: KClass<T>, handler: (T, DefaultWebSocketSession) -> Unit) {
        map[exceptionClass] = handler as (Exception, DefaultWebSocketSession) -> Unit
    }
}
