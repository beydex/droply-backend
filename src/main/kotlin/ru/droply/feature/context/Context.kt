package ru.droply.feature.context

import ru.droply.feature.context.auth.Auth

interface Context {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)

    fun <T> ret(key: String): T?

    var auth: Auth?
}