package ru.droply.feature.ext

import ru.droply.feature.context.Context


inline fun <reified T : Any> Context.retCreate(key: String, supply: () -> T): T {
    var result = ret<T>(key)
    if (result == null) {
        result = supply()
        set(key, result)
    }

    return result
}

inline fun <reified T : Any> Context.retDefault(key: String, supply: () -> T): T {
    return ret<T>(key) ?: supply()
}