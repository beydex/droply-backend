package ru.droply.scenes.endpoint.contact

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.mapper.DroplyContactMapper
import ru.droply.service.DroplyContactService
import ru.droply.service.DroplyUserService
import ru.droply.service.extensions.auth
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class ContactDeleteInDto(val id: Long)

@DroplyScene("contact/delete")
@AuthRequired
class ContactDeleteScene : RestScene<ContactDeleteInDto, ContactListOutDto>(
    ContactDeleteInDto.serializer(),
    ContactListOutDto.serializer()
) {
    @Autowired
    private lateinit var contactMapper: DroplyContactMapper

    @Autowired
    private lateinit var contactService: DroplyContactService

    @Autowired
    private lateinit var userService: DroplyUserService

    override fun DefaultWebSocketSession.handle(request: ContactDeleteInDto, nonce: UUID): ContactListOutDto {
        val user = userService.findByIdAndFetchContacts(ctx.auth!!.user.id!!)
            ?: throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)

        val contactUser = userService.findById(request.id)
            ?: throw DroplyException(code = DroplyErrorCode.NOT_FOUND)

        val contact = contactService.getContact(user, contactUser)
            ?: throw DroplyException(code = DroplyErrorCode.NOT_FOUND)

        user.contacts.remove(contact)
        contactService.remove(contact)
        return ContactListOutDto(success = true, entries = user.contacts.map(contactMapper::map))
    }
}
