package ru.droply.scene.code

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import ru.droply.scenes.endpoint.code.DroplyCodeOutDto
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import ru.droply.test.useAuthUser
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class CodeRefreshSceneTest : DroplyTest() {
    @Test
    fun `call scene with no auth failure`() {
        socketIncoming(makeRequest("code/refresh")) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
            }
        }
    }

    @Test
    fun `call scene with no code get unique code success`() {
        useAuthUser { droplyUser ->
            socketIncoming(makeRequest("code/refresh")) {
                assertReceive<DroplyCodeOutDto>(it).apply {
                    val userByReceivedCode = userService.findByUrid(code)
                    assertNotNull(userByReceivedCode)
                    assertEquals(droplyUser.id, userByReceivedCode.id)
                }
            }
        }
    }

    @Test
    fun `call scene with granted code get unique code success`() {
        useAuthUser { droplyUser ->
            val oldCode = userService.updateUserUrid(droplyUser)

            socketIncoming(makeRequest("code/refresh")) {
                val response = assertReceive<DroplyCodeOutDto>(it)

                assertNotEquals(oldCode, response.code)
                val userByReceivedCode = withContext(Dispatchers.IO) {
                    userService.findByUrid(response.code)
                }

                assertNotNull(userByReceivedCode)
                assertEquals(droplyUser.id, userByReceivedCode.id)
            }
        }
    }
}
