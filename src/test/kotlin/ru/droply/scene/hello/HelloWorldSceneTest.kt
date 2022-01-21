package ru.droply.scene.hello

import org.junit.jupiter.api.Test
import ru.droply.scene.test.HelloRequest
import ru.droply.scene.test.HelloResponse
import ru.droply.scene.test.WorldResponse
import ru.droply.test.*
import kotlin.test.assertEquals

class HelloWorldSceneTest : DroplyTest() {

    @Test
    fun `call hello scene with valid body check response success`() {
        socketIncoming(makeRequest("hello", HelloRequest("theseems"))) {
            assertEquals(
                "Welcome to Droply, theseems",
                assertReceive<HelloResponse>(it).message
            )
        }
    }

    @Test
    fun `call world scene check response success`() {
        context["name"] = null
        socketIncoming(makeRequest("world")) {
            assertEquals(
                "hi, unknown",
                assertReceive<WorldResponse>(it).message
            )
        }
    }

    @Test
    fun `call world scene with name context check response success`() {
        context["name"] = "someone"
        socketIncoming(makeRequest("world")) {
            assertEquals(
                "hi, someone",
                assertReceive<WorldResponse>(it).message
            )
        }
    }

    @Test
    fun `call hello set name then call world check response success`() {
        socket(makeRequest("hello", HelloRequest("theseems"))) { _, _ ->
            socketIncoming(makeRequest("world")) {
                assertEquals(
                    "hi, theseems",
                    assertReceive<WorldResponse>(it).message
                )
            }
        }
    }
}