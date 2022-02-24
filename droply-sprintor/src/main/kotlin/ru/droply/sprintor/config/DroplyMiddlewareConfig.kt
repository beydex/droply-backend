package ru.droply.sprintor.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import ru.droply.sprintor.middleware.DroplyMiddleware

@Configuration
@ComponentScan("ru.droply.sprintor.middleware")
class DroplyMiddlewareConfig {
    @Autowired
    private lateinit var context: ApplicationContext
    private val logger = KotlinLogging.logger {}

    @Bean
    fun middlewareCollection(): Collection<DroplyMiddleware> {
        return context.getBeansOfType(DroplyMiddleware::class.java)
            .onEach { (beanName, _) -> logger.info { "Scanned middleware: $beanName" } }
            .values
    }
}
