package ru.droply.service

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.droply.data.common.auth.AuthPayload
import ru.droply.service.config.DroplyJwtConfig
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class JwtService {
    @Autowired
    private lateinit var verifier: JWTVerifier

    @Autowired
    private lateinit var issuer: DroplyJwtConfig.JWTIssuer

    private val json = Json { ignoreUnknownKeys = true }

    fun decodeAuthToken(token: String): AuthPayload? {
        return try {
            verifier.verify(token).payload
                .let { String(Base64.getDecoder().decode(it)) }
                .let { json.decodeFromString(AuthPayload.serializer(), it) }
        } catch (exception: JWTVerificationException) {
            null
        }
    }

    fun issueAuthToken(
        authPayload: AuthPayload,
        expiresAt: Instant = Instant.now().plus(30, ChronoUnit.DAYS)
    ) = issuer.issue(asMap(authPayload), expiresAt)
}

fun asMap(value: Any): Map<String, Any?> {
    val mapper = ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    return mapper.readValue<Map<String, Any>>(
        mapper.writeValueAsString(value),
        object : TypeReference<Map<String, Any>>() {}
    )
}
