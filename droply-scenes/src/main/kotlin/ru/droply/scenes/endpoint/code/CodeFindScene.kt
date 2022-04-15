package ru.droply.scenes.endpoint.code

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.dto.DroplyUserGeneralOutDto
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class DroplyCodeFindInDto(val code: Int)

@Serializable
data class DroplyCodeFindOutDto(val success: Boolean, val user: DroplyUserGeneralOutDto)

@DroplyScene("code/find")
class CodeFindScene : RestScene<DroplyCodeFindInDto, DroplyCodeFindOutDto>(
    DroplyCodeFindInDto.serializer(),
    DroplyCodeFindOutDto.serializer()
) {
    @Autowired
    private lateinit var userService: DroplyUserService

    @Autowired
    private lateinit var userMapper: DroplyUserMapper

    override fun DefaultWebSocketSession.handle(request: DroplyCodeFindInDto): DroplyCodeFindOutDto {
        val user = userService.findByUrid(request.code)
            ?: throw DroplyException(DroplyErrorCode.NOT_FOUND)

        return DroplyCodeFindOutDto(
            success = true,
            user = userMapper.mapToGeneral(user)
        )
    }
}
