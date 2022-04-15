package ru.droply.connector

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.auth.AuthPayload
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.scenes.endpoint.auth.AuthInDto
import ru.droply.scenes.endpoint.auth.AuthOutDto
import ru.droply.service.JwtService
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.makeUser
import ru.droply.test.socketIncoming
import kotlin.test.assertNotNull

class ConnectorTest : DroplyTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    @Autowired
    private lateinit var droplyLocator: DroplyLocator

    @Test
    fun `auth then locate self success`() {
        val user = makeUser()
        val token = jwtService.issueAuthToken(AuthPayload(AuthProvider.CUSTOM, userMapper.map(user)))

        socketIncoming(makeRequest("auth", AuthInDto(token))) { incoming ->
            assertReceive<AuthOutDto>(incoming).apply { assert(success) }
            assertNotNull(droplyLocator.lookupUser(user.id!!))
        }
    }
}