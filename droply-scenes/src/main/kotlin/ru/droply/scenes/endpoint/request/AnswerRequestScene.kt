package ru.droply.scenes.endpoint.request

import io.ktor.http.cio.websocket.DefaultWebSocketSession
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
data class RequestAnswerInDto(val requestId: Long, val accept: Boolean, var answer: String? = null)

@Serializable
data class RequestAnswerOutDto(val success: Boolean)

@DroplyScene("request/answer")
@AuthRequired
class AnswerRequestScene :
    RestScene<RequestAnswerInDto, RequestAnswerOutDto>(
        RequestAnswerInDto.serializer(),
        RequestAnswerOutDto.serializer()
    ) {

    @Autowired
    private lateinit var requestService: DroplyRequestService

    override fun DefaultWebSocketSession.handle(request: RequestAnswerInDto): RequestAnswerOutDto {
        // Allow either both accept and answer or neither of them
        if (request.accept xor (request.answer == null)) {
            throw DroplyException(code = DroplyErrorCode.BAD_REQUEST)
        }

        val droplyRequest = requestService.findRequest(request.requestId)
        if (droplyRequest == null || droplyRequest.receiver.id != ctx.storedAuth.user.id) {
            throw DroplyException(code = DroplyErrorCode.NOT_FOUND)
        }

        if (!request.accept) {
            requestService.removeRequest(droplyRequest, false)
        } else {
            droplyRequest.active = true
        }

        return RequestAnswerOutDto(success = true)
    }
}
