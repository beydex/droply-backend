package ru.droply.scenes.endpoint.profile

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.dto.DroplyUserOutDto
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.OutRestScene

@Serializable
data class ProfileOutDto(val success: Boolean, val user: DroplyUserOutDto)

@DroplyScene("profile")
@AuthRequired
class ProfileScene : OutRestScene<ProfileOutDto>(ProfileOutDto.serializer()) {
    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    override fun DefaultWebSocketSession.handle(request: Unit): ProfileOutDto = ProfileOutDto(
        success = true,
        user = userMapper.map(ctx.auth!!.user),
    )

}
