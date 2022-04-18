package ru.droply.sprintor.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

lateinit var springContext: ApplicationContext

@Component
private class SpringContextConfigurer : ApplicationContextAware {
    override fun setApplicationContext(context: ApplicationContext) {
        springContext = context
    }
}
