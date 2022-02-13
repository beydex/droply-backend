package ru.droply.sprintor.context

import ru.droply.data.common.auth.Auth

interface Context {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)

    fun <T> ret(key: String): T?

    var auth: Auth?
}
