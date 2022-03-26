package ru.droply.scenes.endpoint.test

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Profile
import ru.droply.sprintor.middleware.validation.ValidationRequired
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.scene.variety.RestScene

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
    val success: Boolean
)

typealias Request = ValidationRequest
typealias Response = ValidationResponse

@Profile("test")
@DroplyScene("test/validation")
@ValidationRequired
class ValidationScene : RestScene<Request, Response>(Request.serializer(), Response.serializer()) {
    override fun DefaultWebSocketSession.handle(request: Request) = Response(success = true)
}
