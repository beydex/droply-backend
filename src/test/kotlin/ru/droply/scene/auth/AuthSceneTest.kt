package ru.droply.scene.auth

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.entity.DroplyUser
import ru.droply.feature.context.auth.AuthPayload
import ru.droply.feature.context.auth.AuthProvider
import ru.droply.mapper.DroplyUserMapper
import ru.droply.service.DroplyUserService
import ru.droply.service.JwtService
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthSceneTest : DroplyTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userMapper: DroplyUserMapper

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
}