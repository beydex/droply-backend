package ru.droply.scenes.endpoint.contact

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.common.dto.DroplyUserContactOutDto
import ru.droply.scenes.endpoint.mapper.DroplyContactMapper
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.OutRestScene
import ru.droply.sprintor.serializer.KZonedDateTimeSerializer

@Serializable
data class ContactEntryOutDto(
    val user: DroplyUserContactOutDto,

    @Serializable(KZonedDateTimeSerializer::class)
    val lastSuccessRequestDate: ZonedDateTime
)

@Serializable
data class ContactListOutDto(val success: Boolean, val entries: List<ContactEntryOutDto>)

@DroplyScene("contact/list")
@AuthRequired
class GetContactListScene : OutRestScene<ContactListOutDto>(ContactListOutDto.serializer()) {
    @Autowired
    private lateinit var contactMapper: DroplyContactMapper

    @Autowired
    private lateinit var userService: DroplyUserService

    override fun DefaultWebSocketSession.handle(request: Unit): ContactListOutDto {
        val entries = userService.findByIdAndFetchContacts(ctx.auth!!.user.id!!)!!
            .contacts
            .map(contactMapper::map)

        return ContactListOutDto(success = true, entries)
    }

}