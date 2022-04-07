package ru.droply.scene.profile

import org.junit.jupiter.api.Test
import ru.droply.data.common.dto.DroplyUserOutDto
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import ru.droply.test.useAuthUser
import kotlin.test.assertEquals

class ProfileSceneTest: DroplyTest() {

    @Test
    fun `call profile with auth success`() {
        useAuthUser { user ->
            socketIncoming(makeRequest("profile")) {
                val result = assertReceive<DroplyUserOutDto>(it)
                assertEquals(user.urid, result.urid)
            }
        }
    }

}