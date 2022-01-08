package ru.droply.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.droply.feature.context.ConnectionPool
import ru.droply.feature.context.MemoryConnectionPool
import ru.droply.feature.subrouting.MemorySceneManager
import ru.droply.feature.subrouting.SceneManager

@Configuration
class DroplyConfig {
    @Bean
    fun connectionPool(): ConnectionPool {
        return MemoryConnectionPool()
    }

    @Bean
    fun sceneManager(): SceneManager {
        return MemorySceneManager()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}