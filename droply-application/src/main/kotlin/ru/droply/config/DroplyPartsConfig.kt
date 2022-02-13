package ru.droply.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import ru.droply.sprintor.context.ConnectionPool
import ru.droply.sprintor.context.MemoryConnectionPool
import ru.droply.sprintor.scene.MemorySceneManager
import ru.droply.sprintor.scene.SceneManager

@Profile("!test")
@Configuration
class DroplyPartsConfig {
    @Bean
    fun connectionPool(): ConnectionPool = MemoryConnectionPool()

    @Bean
    fun sceneManager(): SceneManager = MemorySceneManager()
}
