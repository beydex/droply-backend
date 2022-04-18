package ru.droply.test

import ru.droply.sprintor.context.MemoryContext

class TestContext : MemoryContext() {
    fun clear() {
        map.clear()
    }

    override fun toString(): String {
        return "TestContext(${super.toString()})"
    }
}
