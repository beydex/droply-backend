package ru.droply.test

import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import ru.droply.DroplyApplication
import ru.droply.feature.spring.autowired


@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = [DroplyApplication::class])
class DroplyTest {
    var context: TestContext by autowired()

    // Refresh context between tests (on refresh)
    @EventListener
    fun handleContextRefreshEvent(ctxRefreshedEvent: ContextRefreshedEvent) {
        context = ctxRefreshedEvent.applicationContext.getBean(TestContext::class.java)
    }

    @BeforeEach
    fun clearContext() {
        context.clear()
    }
}