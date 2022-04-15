package ru.droply.sprintor.spring

import kotlin.reflect.KProperty

fun context() = Context()

class Context {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = springContext
}