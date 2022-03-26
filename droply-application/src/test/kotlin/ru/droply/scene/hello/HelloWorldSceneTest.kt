package ru.droply.scene.hello

import org.junit.jupiter.api.Test
import ru.droply.scenes.endpoint.test.HelloInDto
import ru.droply.scenes.endpoint.test.HelloOutDto
import ru.droply.scenes.endpoint.test.WorldResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socket
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals

class HelloWorldSceneTest : DroplyTest() {
    @Test
    fun `call hello scene with valid body check response success`() {
        socketIncoming(makeRequest("test/hello", HelloInDto("theseems"))) {
            assertEquals(
                "Welcome to Droply, theseems",
                assertReceive<HelloOutDto>(it).message
            )
        }
    }

    @Test
    fun `call world scene check response success`() {
        context["name"] = null
        socketIncoming(makeRequest("test/world")) {
            assertEquals(
                "hi, unknown",
                assertReceive<WorldResponse>(it).message
            )
        }
    }

    @Test
    fun `call world scene with name context check response success`() {
        context["name"] = "someone"
        socketIncoming(makeRequest("test/world")) {
            assertEquals(
                "hi, someone",
                assertReceive<WorldResponse>(it).message
            )
        }
    }

    @Test
    fun `call hello set name then call world check response success`() {
        socket(makeRequest("test/hello", HelloInDto("theseems"))) { _, _ ->
            socketIncoming(makeRequest("test/world")) {
                assertEquals(
                    "hi, theseems",
                    assertReceive<WorldResponse>(it).message
                )
            }
        }
    }
}
