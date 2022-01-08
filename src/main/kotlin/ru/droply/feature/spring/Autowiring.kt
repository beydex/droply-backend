package ru.droply.feature.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty

private lateinit var ctx: ApplicationContext

@Component
private class CtxVarConfigurer : ApplicationContextAware {
    override fun setApplicationContext(context: ApplicationContext) {
        ctx = context
    }
}

inline fun <reified T : Any> autowired(name: String? = null) = Autowired(T::class.java, name)

class Autowired<T : Any>(private val javaType: Class<T>, private val name: String?) {
    private val value by lazy {
        if (name == null) {
            ctx.getBean(javaType)
        } else {
            ctx.getBean(name, javaType)
        }
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}