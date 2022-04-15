package ru.droply.connector

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.auth.AuthPayload
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.scenes.endpoint.auth.AuthInDto
import ru.droply.scenes.endpoint.auth.AuthOutDto
import ru.droply.scenes.endpoint.auth.LogoutSceneOutDto
import ru.droply.service.JwtService
import ru.droply.sprintor.event.UserAuthorizeEvent
import ru.droply.sprintor.event.UserLogoutEvent
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.listenFor
import ru.droply.test.makeRequest
import ru.droply.test.makeUser
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserAuthEventTest : DroplyTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    @Test
    fun `call jwt auth scene then check event success`() {
        val user = makeUser()
        val token = jwtService.issueAuthToken(AuthPayload(AuthProvider.CUSTOM, userMapper.map(user)))

        val event = listenFor<UserAuthorizeEvent> {
            socketIncoming(makeRequest("auth", AuthInDto(token))) {
                assertReceive<AuthOutDto>(it).apply { assert(success) }
            }
        }

        assertEquals(user.id, event.user.id)
    }

    @Test
    fun `call jwt auth scene then logout check event success`() {
        val user = makeUser()
        val token = jwtService.issueAuthToken(AuthPayload(AuthProvider.CUSTOM, userMapper.map(user)))

        socketIncoming(makeRequest("auth", AuthInDto(token))) { incoming ->
            assertReceive<AuthOutDto>(incoming).apply { assert(success) }

            val event = listenFor<UserLogoutEvent> {
                socketIncoming(makeRequest("auth/logout")) {
                    assertReceive<LogoutSceneOutDto>(it).apply { assert(success) }
                    assertNull(context.auth)
                }
            }

            assertEquals(user.id, event.user.id)
        }
    }
}