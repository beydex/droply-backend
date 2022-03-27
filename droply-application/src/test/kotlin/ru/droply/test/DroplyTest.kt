package ru.droply.test

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils
import ru.droply.config.DroplyTestAuthConfig
import ru.droply.config.DroplyTestJpaConfig
import ru.droply.config.DroplyTestJwtConfig
import ru.droply.config.DroplyTestPartsConfig
import ru.droply.service.DroplyUserService

@ActiveProfiles("test")
@SpringBootTest(
    classes = [
        DroplyTestJpaConfig::class,
        DroplyTestJwtConfig::class,
        DroplyTestPartsConfig::class,
        DroplyTestAuthConfig::class
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DroplyTest {
    @Autowired
    lateinit var userService: DroplyUserService

    @Autowired
    lateinit var context: TestContext

    // Refresh context between tests (on refresh)
    @EventListener
    fun handleContextRefreshEvent(ctxRefreshedEvent: ContextRefreshedEvent) {
        context = ctxRefreshedEvent.applicationContext.getBean(TestContext::class.java)
    }

    @BeforeEach
    fun clearContext() {
        context.clear()
    }

    fun injectValue(target: Any, field: String, value: Any, logic: Runnable) {
        val original = ReflectionTestUtils.getField(target, field)
        try {
            ReflectionTestUtils.setField(target, field, value)
            logic.run()
        } finally {
            ReflectionTestUtils.setField(target, field, original)
        }
    }
}
