package ru.droply.scene.auth

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthPayload
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.entity.DroplyUser
import ru.droply.mapper.AuthPayloadMapper
import ru.droply.mapper.DroplyUserMapper
import ru.droply.scenes.endpoint.auth.AuthInDto
import ru.droply.scenes.endpoint.auth.AuthOutDto
import ru.droply.scenes.endpoint.auth.AuthScene
import ru.droply.scenes.endpoint.code.DroplyCodeOutDto
import ru.droply.scenes.endpoint.profile.ProfileOutDto
import ru.droply.service.DroplyUserService
import ru.droply.service.JwtService
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.makeUser
import ru.droply.test.socketIncoming
import ru.droply.test.useAuthUser
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthSceneTest : DroplyTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    @Autowired
    private lateinit var authPayloadMapper: AuthPayloadMapper

    @Autowired
    private lateinit var authScene: AuthScene

    @Test
    fun `issue jwt then call auth success`() {
        val misterX = DroplyUser("X", "x@grandson.us")

        val token = jwtService.issueAuthToken(
            AuthPayload(
                AuthProvider.GOOGLE,
                userMapper.map(misterX)
            )
        )

        assertNotNull(token)

        val userService = mock<DroplyUserService>()
        whenever(userService.findByEmail(any())).thenReturn(misterX)

        injectValue(authScene, "userService", userService) {
            socketIncoming(makeRequest("auth", AuthInDto(token))) {
                val response = assertReceive<AuthOutDto>(it)
                assert(response.success) { "Failed response: $response" }
                assertEquals("Authed in X", response.message)
            }
        }
    }

    @Test
    fun `receive jwt with urid then call auth then verify user success`() {
        val user = makeUser().apply { urid = 1213 }
        val token = jwtService.issueAuthToken(authPayloadMapper.map(Auth(AuthProvider.CUSTOM, user)))

        socketIncoming(makeRequest("auth", AuthInDto(token))) {
            assertReceive<AuthOutDto>(it).apply {
                assert(success)
            }
        }
    }

    @Test
    fun `check user data update in context`() {
        useAuthUser { droplyUser ->
            var newCode: Int? = null
            socketIncoming(makeRequest("code/refresh")) {
                assertReceive<DroplyCodeOutDto>(it).apply {
                    assert(success)
                    newCode = code
                }
            }
            socketIncoming(makeRequest("profile")) {
                assertReceive<ProfileOutDto>(it).apply {
                    assertEquals(user.urid, newCode)
                }
            }
        }
    }
}
