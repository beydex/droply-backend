package ru.droply.config

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import ru.droply.feature.processor.ExceptionHandlerContainer
import ru.droply.feature.processor.ExceptionProcessor
import ru.droply.feature.processor.HashAndHierarchyExceptionProcessor
import java.util.stream.Collectors
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

@Configuration
@ComponentScan("ru.droply.feature.processor")
class DroplyExceptionConfig {
    @Autowired
    private lateinit var context: ApplicationContext
    private val logger = KotlinLogging.logger {}

    @Bean
    fun exceptionProcessor(): ExceptionProcessor {
        val processor = HashAndHierarchyExceptionProcessor()

        context.getBeansWithAnnotation(ExceptionHandlerContainer::class.java).forEach {
            val container = it.value

            container::class.memberFunctions.filter { function ->
                function.parameters.size == 3 &&
                    function.parameters[1].type.isSubtypeOf(Exception::class.createType()) &&
                    function.parameters[2].type.isSubtypeOf(DefaultWebSocketSession::class.createType())
            }.forEach { function ->
                @Suppress("UNCHECKED_CAST")
                val exceptionClass = function.parameters[1].type.classifier as KClass<out Exception>
                processor.append(exceptionClass) { exception, session ->
                    function.call(container, exception, session, null)
                }

                val handlerFunctionDeclaration =
                    function.parameters.stream()
                        .skip(1) // Skip instance parameter
                        .map { param -> param.type.toString() }
                        .collect(Collectors.joining(","))

                logger.info {
                    "Append ${function.name}($handlerFunctionDeclaration) of ${container::class.simpleName}"
                }
            }
        }

        return processor
    }
}
