package ru.droply.scene.auth

import org.junit.jupiter.api.Test
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.entity.DroplyUser
import ru.droply.scenes.endpoint.auth.LogoutSceneOutDto
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LogoutSceneTest : DroplyTest() {
    @Test
    fun `call logout without auth failure`() {
        socketIncoming(makeRequest("auth/logout")) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
            }
        }
    }

    @Test
    fun `call logout with auth success`() {
        context.auth = Auth(
            user = DroplyUser("Sbor", "sbor@babla.ru"),
            provider = AuthProvider.CUSTOM
        )

        socketIncoming(makeRequest("auth/logout")) {
            assertReceive<LogoutSceneOutDto>(it).apply {
                assert(success)
            }
        }

        assertNull(context.auth)
        socketIncoming(makeRequest("auth/logout")) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
            }
        }
    }
}
