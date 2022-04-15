package ru.droply.test

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

inline fun <reified T : ApplicationEvent> DroplyTest.listenFor(crossinline logic: () -> Any): T {
    val listener = mock<ApplicationListener<T>>()

    var result: T? = null
    var count = 0

    whenever(listener.onApplicationEvent(any())).then {
        count++
        val arg = it.arguments[0]

        assert(arg is T?)
        result = arg as? T?

        return@then null
    }

    applicationContext.addApplicationListener(listener)
    logic()
    applicationEventMulticaster.removeApplicationListener(listener)

    assertNotNull(result)
    assertEquals(1, count)
    return result as T
}