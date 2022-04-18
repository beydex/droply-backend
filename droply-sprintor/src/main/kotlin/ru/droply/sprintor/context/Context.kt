package ru.droply.sprintor.context

interface Context {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)

    fun <T> ret(key: String): T?
}
