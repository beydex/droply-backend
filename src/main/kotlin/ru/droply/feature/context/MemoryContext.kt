package ru.droply.feature.context

import ru.droply.feature.context.auth.Auth

open class MemoryContext : Context {
    override var auth: Auth? = null
    val map: MutableMap<String, Any> = HashMap()

    override fun get(key: String): Any? {
        return map[key]
    }

    override fun set(key: String, value: Any?) {
        if (key == "auth" && value is Auth) {
            auth = value
            return
        }

        if (value == null) {
            map.remove(key)
            return
        }

        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> ret(key: String): T? {
        return (map[key] ?: return null) as? T
    }

    override fun toString(): String {
        return "MemoryContext(map=$map, auth=$auth)"
    }
}
