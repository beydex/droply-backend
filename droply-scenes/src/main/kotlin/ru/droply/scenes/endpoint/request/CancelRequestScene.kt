package ru.droply.scenes.endpoint.request

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.UUID
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.service.DroplyRequestService
import ru.droply.service.extensions.storedAuth
import ru.droply.sprintor.ktor.ctx
import ru.droply.sprintor.middleware.security.AuthRequired
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

@Serializable
data class CancelRequestInDto(
    val requestId: Long
)

@Serializable
data class CancelRequestOutDto(
    val success: Boolean
)

@DroplyScene("request/cancel")
@AuthRequired
class CancelRequestScene :
    RestScene<CancelRequestInDto, CancelRequestOutDto>(
        CancelRequestInDto.serializer(),
        CancelRequestOutDto.serializer()
    ) {
    @Autowired
    private lateinit var requestService: DroplyRequestService

    override fun DefaultWebSocketSession.handle(request: CancelRequestInDto, nonce: UUID): CancelRequestOutDto {
        val droplyRequest = requestService.findRequest(request.requestId)
        val userId = ctx.storedAuth.user.id
        if (droplyRequest == null || (userId != droplyRequest.sender.id && userId != droplyRequest.receiver.id)) {
            throw DroplyException(code = DroplyErrorCode.NOT_FOUND)
        }

        requestService.removeRequest(droplyRequest, ctx.storedAuth.user, false)
        return CancelRequestOutDto(success = true)
    }
}
