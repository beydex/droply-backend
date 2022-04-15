package ru.droply.scene.code

import org.junit.jupiter.api.Test
import ru.droply.scenes.endpoint.code.DroplyCodeFindInDto
import ru.droply.scenes.endpoint.code.DroplyCodeFindOutDto
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.makeUser
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CodeFindSceneTest : DroplyTest() {
    @Test
    fun `call scene with no code failure`() {
        socketIncoming(makeRequest("code/find")) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.MALFORMED_REQUEST, code)
            }
        }
    }

    @Test
    fun `generate code then call scene with code then found success`() {
        val source = makeUser(email = "code@droply.ru")
        val code = userService.updateUserUrid(source)

        assertNull(context.auth)
        socketIncoming(makeRequest("code/find", DroplyCodeFindInDto(code))) {
            assertReceive<DroplyCodeFindOutDto>(it).apply {
                assert(success)
                assertEquals(code, user.urid)
                assertEquals(source.name, user.name)
            }
        }
    }
}