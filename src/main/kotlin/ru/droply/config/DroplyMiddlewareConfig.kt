package ru.droply.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import ru.droply.feature.middleware.DroplyMiddleware

@Configuration
@ComponentScan("ru.droply.feature.middleware")
class DroplyMiddlewareConfig {
    @Autowired
    private lateinit var context: ApplicationContext
    private val logger = KotlinLogging.logger {}

    @Bean
    fun middlewareList(): List<DroplyMiddleware> {
        return context.getBeansOfType(DroplyMiddleware::class.java)
            .onEach { logger.info { "Using ${it.key}: ${it.value.javaClass.name}" } }
            .values
            .toList()
    }
}
