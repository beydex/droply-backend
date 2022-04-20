package ru.droply.scenes.endpoint.request

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import javax.validation.constraints.Size
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import ru.droply.data.entity.DroplyRequestConstraints
import ru.droply.service.DroplyRequestService
import ru.droply.service.extensions.storedAuth
import ru.droply.sprintor.event.UserRequestSignalEvent
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.middleware.validation.ValidationRequired
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class RequestSignalInDto(
    val requestId: Long,
    @field:Size(max = DroplyRequestConstraints.MAX_SIGNALING_CONTENT_SIZE)
    val content: String
)

@Serializable
data class RequestSignalOutDto(val success: Boolean)

@DroplyScene("request/signal")
@AuthRequired
@ValidationRequired
class RequestSignalScene :
    RestScene<RequestSignalInDto, RequestSignalOutDto>(
        RequestSignalInDto.serializer(),
        RequestSignalOutDto.serializer()
    ) {

    @Autowired
    private lateinit var requestService: DroplyRequestService

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    override fun DefaultWebSocketSession.handle(request: RequestSignalInDto): RequestSignalOutDto {
        val droplyRequest = requestService.findRequest(request.requestId)
        val userId = ctx.storedAuth.user.id

        if (droplyRequest == null || !droplyRequest.active ||
            (userId != droplyRequest.sender.id && userId != droplyRequest.receiver.id)
        ) {
            throw DroplyException(code = DroplyErrorCode.NOT_FOUND)
        }

        eventPublisher.publishEvent(UserRequestSignalEvent(droplyRequest, ctx.storedAuth.user, request.content))
        return RequestSignalOutDto(success = true)
    }
}
