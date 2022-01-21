package ru.droply.feature.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.properties.ReadWriteProperty
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
    private var value by LazyMutable {
        if (name == null) {
            ctx.getBean(javaType)
        } else {
            ctx.getBean(name, javaType)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        value = newValue
    }
}

class LazyMutable<T>(
    val initializer: () -> T,
) : ReadWriteProperty<Any?, T> {
    private val lazyValue by lazy { initializer() }
    private var newValue: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        newValue ?: lazyValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        newValue = value
    }
}