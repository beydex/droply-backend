package ru.droply.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.feature.context.ConnectionPool
import ru.droply.feature.ktor.connectionPool
import ru.droply.feature.scene.MemorySceneManager
import ru.droply.feature.scene.SceneManager
import ru.droply.test.TestContext

@Profile("test")
@Configuration
class DroplyTestPartsConfig {
    @Bean
    fun sceneManager(): SceneManager = MemorySceneManager()

    @Bean
    fun testContext(): TestContext = TestContext()

    // Refreshing global pool between tests
    @Component
    class ConnectionPoolRefresher {
        @EventListener
        fun handleContextRefreshEvent(ctxRefreshedEvent: ContextRefreshedEvent) {
            connectionPool = ctxRefreshedEvent.applicationContext.getBean("singletonContextPool") as ConnectionPool
        }
    }
}