package ru.droply.scene.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import ru.droply.feature.scene.variety.RestScene
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

// Source: https://www.baeldung.com/java-regex-validate-phone-numbers#multiple
const val PHONE_REGEX: String =
    "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}\$"

@Serializable
data class ValidationRequest(
    @field:Pattern(regexp = PHONE_REGEX, message = "phone invalid")
    val phone: String,
    @field:Email(message = "email invalid")
    val email: String
)

@Serializable
data class ValidationResponse(
    val success: Boolean,
    val errors: List<String>
)

typealias Request = ValidationRequest
typealias Response = ValidationResponse

@Component
class ValidationScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Request): Response {
        return Response(success = true, errors = listOf())
    }
}
