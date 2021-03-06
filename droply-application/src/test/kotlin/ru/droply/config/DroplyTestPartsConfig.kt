package ru.droply.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.droply.sprintor.context.ConnectionPool
import ru.droply.sprintor.ktor.connectionPool
import ru.droply.sprintor.scene.MemorySceneManager
import ru.droply.sprintor.scene.SceneManager
import ru.droply.test.TestContext

@TestConfiguration
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
