package ru.droply.sprintor.context

open class MemoryContext : Context {
    val map: MutableMap<String, Any> = HashMap()

    override fun get(key: String): Any? {
        return map[key]
    }

    override fun set(key: String, value: Any?) {

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
        return "MemoryContext(map=$map)"
    }
}
