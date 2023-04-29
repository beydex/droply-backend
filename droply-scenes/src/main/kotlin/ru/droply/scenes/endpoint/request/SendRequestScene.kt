package ru.droply.scenes.endpoint.request

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import javax.validation.constraints.Size
import kotlinx.serialization.Serializable
import org.hibernate.validator.constraints.Length
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import ru.droply.data.common.dto.request.DroplyFileDto
import ru.droply.data.common.dto.request.RequestOutDto
import ru.droply.data.entity.DroplyRequestConstraints
import ru.droply.mapper.DroplyFileMapper
import ru.droply.mapper.DroplyRequestMapper
import ru.droply.service.DroplyRequestService
import ru.droply.service.DroplyUserService
import ru.droply.service.extensions.storedAuth
import ru.droply.sprintor.event.UserRequestSendEvent
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.middleware.validation.ValidationRequired
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class RequestSendInDto(
    val receiverId: Long? = null,
    val receiverUrid: Int? = null,

    @field:Length(
        min = DroplyRequestConstraints.MIN_OFFER_LENGTH,
        max = DroplyRequestConstraints.MAX_OFFER_LENGTH
    )
    val offer: String,

    @field:Size(
        min = DroplyRequestConstraints.MIN_FILES_SIZE,
        max = DroplyRequestConstraints.MAX_FILES_SIZE
    )
    val files: Set<DroplyFileDto>
)

@Serializable
data class RequestSendOutDto(val success: Boolean, val request: RequestOutDto)

@DroplyScene("request/send")
@AuthRequired
@ValidationRequired
class SendRequestScene :
    RestScene<RequestSendInDto, RequestSendOutDto>(RequestSendInDto.serializer(), RequestSendOutDto.serializer()) {

    @Autowired
    private lateinit var userService: DroplyUserService

    @Autowired
    private lateinit var requestService: DroplyRequestService

    @Autowired
    private lateinit var requestMapper: DroplyRequestMapper

    @Autowired
    private lateinit var fileMapper: DroplyFileMapper

    @field:Value("\${droply.requestLimits.incoming}")
    private var incomingLimit: Int = 3

    @field:Value("\${droply.requestLimits.outgoing}")
    private var outgoingLimit: Int = 3

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    override fun DefaultWebSocketSession.handle(request: RequestSendInDto, nonce: UUID): RequestSendOutDto {
        // If both or neither of them are provided
        if (!((request.receiverId != null) xor (request.receiverUrid != null))) {
            throw DroplyException(code = DroplyErrorCode.BAD_REQUEST)
        }

        val sender = userService.findFetchOutgoingRequests(ctx.storedAuth.user.id!!)
            ?: throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)

        var receiverId = request.receiverId
        if (receiverId == null) {
            receiverId = userService.findByUrid(request.receiverUrid!!)?.id
                ?: throw DroplyException(code = DroplyErrorCode.NOT_FOUND)
        }

        val receiver = userService.findFetchIncomingRequests(receiverId)
            ?: throw DroplyException(code = DroplyErrorCode.NOT_FOUND)

        if (sender.outgoingRequests.filter { !it.active }.size >= outgoingLimit) {
            throw DroplyException(code = DroplyErrorCode.TOO_MANY_REQUESTS)
        }

        if (receiver.incomingRequests.filter { !it.active }.size >= incomingLimit) {
            throw DroplyException(code = DroplyErrorCode.TOO_MANY_REQUESTS)
        }

        val requestEntity = requestService.sendRequest(
            sender,
            receiver,
            request.offer,
            fileMapper.map(request.files)
        )
        val outDto = RequestSendOutDto(
            success = true,
            request = requestMapper.map(requestEntity)
        )

        applicationEventPublisher.publishEvent(UserRequestSendEvent(requestEntity))
        return outDto
    }
}
