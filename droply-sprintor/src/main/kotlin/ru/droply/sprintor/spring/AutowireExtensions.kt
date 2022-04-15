package ru.droply.sprintor.spring

import ru.droply.sprintor.util.LazyMutable
import kotlin.reflect.KProperty

inline fun <reified T : Any> autowired(name: String? = null) = Autowired(T::class.java, name)

class Autowired<T : Any>(private val javaType: Class<T>, private val name: String?) {
    private var value by LazyMutable {
        if (name == null) {
            springContext.getBean(javaType)
        } else {
            springContext.getBean(name, javaType)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        value = newValue
    }
}
