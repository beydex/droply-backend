package ru.droply.service.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.droply.service.util.PemUtils
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

private const val JWT_SIGN_ALGORITHM: String = "EC"

@Configuration
class DroplyJwtConfig {
    @Value("\${droply.security.keys.public}")
    private lateinit var publicKeyLocation: String

    @Value("\${droply.security.keys.private}")
    private lateinit var privateKeyLocation: String

    @Value("\${droply.security.issuer}")
    private lateinit var issuer: String

    interface JWTIssuer {
        fun issue(
            payload: Map<String, Any?>,
            expiresAt: Instant = Instant.now().plus(30, ChronoUnit.DAYS)
        ): String
    }

    @Bean
    fun publicKey(): ECPublicKey? =
        PemUtils.readPublicKeyFromFile(publicKeyLocation, JWT_SIGN_ALGORITHM) as? ECPublicKey?

    @Bean
    fun privateKey(): ECPrivateKey? =
        PemUtils.readPrivateKeyFromFile(privateKeyLocation, JWT_SIGN_ALGORITHM) as ECPrivateKey?

    @Bean
    fun algorithm(): Algorithm = Algorithm.ECDSA256(publicKey(), privateKey())

    @Bean
    fun jwtVerifier(): JWTVerifier = JWT.require(algorithm()).withIssuer(issuer).build()

    @Bean
    fun jwtIssuer(): JWTIssuer {
        return object : JWTIssuer {
            override fun issue(
                payload: Map<String, Any?>,
                expiresAt: Instant
            ) = JWT.create()
                .withIssuer(issuer)
                .withPayload(payload)
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm())
        }
    }
}
