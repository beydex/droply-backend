package ru.droply.scenes.endpoint.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import ru.droply.data.common.dto.DroplyUserContactOutDto
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class LocateUserInDto(val id: Long)

@Serializable
data class LocateUserOutDto(val success: Boolean, val online: Boolean, val user: DroplyUserContactOutDto?)

@Profile("test-stand")
@DroplyScene("test/locate")
class LocateUserScene :
    RestScene<LocateUserInDto, LocateUserOutDto>(LocateUserInDto.serializer(), LocateUserOutDto.serializer()) {
    @Autowired
    private lateinit var locator: DroplyLocator

    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    @Autowired
    private lateinit var userService: DroplyUserService

    override fun DefaultWebSocketSession.handle(request: LocateUserInDto): LocateUserOutDto {
        val located = locator.lookupUser(request.id)

        return LocateUserOutDto(
            success = true,
            online = located != null,
            user = if (located != null) {
                userService.fetchUser(located.ctx)
                    ?.let { userMapper.mapToContact(it) }
            } else null
        )
    }
}