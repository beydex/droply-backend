package ru.droply.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import ru.droply.feature.context.ConnectionPool
import ru.droply.feature.context.MemoryConnectionPool
import ru.droply.feature.scene.MemorySceneManager
import ru.droply.feature.scene.SceneManager

@Profile("!test")
@Configuration
class DroplyPartsConfig {
    @Bean
    fun connectionPool(): ConnectionPool = MemoryConnectionPool()

    @Bean
    fun sceneManager(): SceneManager = MemorySceneManager()
}
