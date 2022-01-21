package ru.droply.test

import ru.droply.feature.context.MemoryContext

class TestContext: MemoryContext() {
    fun clear() {
        map.clear()
        auth = null
    }

    override fun toString(): String {
        return "TestContext(${super.toString()})"
    }
}