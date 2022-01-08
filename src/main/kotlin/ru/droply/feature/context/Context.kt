package ru.droply.feature.context

interface Context {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any)

    fun <T> ret(key: String): T?

    fun auth(): Auth?
}