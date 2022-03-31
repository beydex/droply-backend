package ru.droply.scene.auth

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.scenes.endpoint.auth.WhoamiOutDto
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import ru.droply.test.useAuthUser
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WhoamiSceneTest : DroplyTest() {
    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    @Test
    fun `call scene with no auth failure`() {
       socketIncoming(makeRequest("auth/whoami")) {
           assertReceive<DroplyErrorResponse>(it).apply {
               assert(!success)
               assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
           }
       }
    }

    @Test
    fun `call scene with present auth and deleted user failure`() {
        useAuthUser { user ->
            userService.removeUserByEmail(user.email)
            socketIncoming(makeRequest("auth/whoami")) {
                assertReceive<DroplyErrorResponse>(it).apply {
                    assert(!success)
                    assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
                }
            }
        }
    }

    @Test
    fun `call scene with present auth and active user success`() {
        useAuthUser { droplyUser ->
            socketIncoming(makeRequest("auth/whoami")) {
                assertReceive<WhoamiOutDto>(it).apply {
                    assert(success)
                    assertNotNull(user)
                    assertEquals(userMapper.map(droplyUser), user!!)
                }
            }
        }
    }
}
