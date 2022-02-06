package ru.droply.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.droply.feature.middleware.DroplyMiddleware
import ru.droply.feature.middleware.variety.ValidationMiddleware

@Configuration
class DroplyMiddlewareConfig {
    @Autowired
    @Qualifier("validation-middleware")
    private lateinit var validation: ValidationMiddleware

    @Bean
    fun middlewareList(): List<DroplyMiddleware> {
        return listOf(
            validation
        )
    }
}
