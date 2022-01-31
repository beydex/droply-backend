package ru.droply.service

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.droply.config.DroplyJwtConfig
import ru.droply.feature.context.auth.AuthPayload
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

    fun issueAuthToken(authPayload: AuthPayload): String? {
        return try {
            issuer.issue(asMap(authPayload))
        } catch (exception: JWTCreationException) {
            exception.printStackTrace()
            return null
        }
    }
}

fun asMap(value: Any): Map<String, Any?> =
    Gson().run { fromJson<Map<String, Any?>>(toJson(value), MutableMap::class.java) }