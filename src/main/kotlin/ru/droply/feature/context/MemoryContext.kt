package ru.droply.feature.context

class MemoryContext : Context {
    private val map: MutableMap<String, Any> = HashMap()
    private var auth: Auth? = null

    override fun get(key: String): Any? {
        return map[key]
    }

    override fun set(key: String, value: Any) {
        if (key == "auth" && value is Auth) {
            auth = value
            return
        }

        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> ret(key: String): T? {
        return (map[key] ?: return null) as? T
    }

    override fun auth(): Auth? {
        return auth
    }
}