package ru.droply.scene

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.droply.feature.spring.autowired
import ru.droply.test.*
import kotlin.test.assertEquals

class HelloWorldSceneTest : DroplyTest() {
    @Nested
    inner class HelloSceneTest {
        @Test
        fun `call hello scene with valid body check response success`() {
            socketIncoming(makeRequest("hello", HelloRequest("theseems"))) {
                assertEquals(
                    "Welcome to Droply, theseems",
                    assertReceive<HelloResponse>(it).message
                )
            }
        }
    }

    @Nested
    inner class WorldSceneTest {
        private val pool: SingletonConnectionPool by autowired()

        @Test
        fun `call world scene check response success`() {
            pool.tweak { set("name", null) }
            socketIncoming(makeRequest("world")) {
                assertEquals(
                    "hi, unknown",
                    assertReceive<WorldResponse>(it).message
                )
            }
        }

        @Test
        fun `call world scene with name context check response success`() {
            pool.tweak { set("name", "something") }
            socketIncoming(makeRequest("world")) {
                assertEquals(
                    "hi, something",
                    assertReceive<WorldResponse>(it).message
                )
            }
        }
    }

    @Test
    fun `call hello set name then call world check response success`() {
        socket(makeRequest("hello", HelloRequest("theseems")))
        socketIncoming(makeRequest("world")) {
            assertEquals(
                "hi, theseems",
                assertReceive<WorldResponse>(it).message
            )
        }
    }
}