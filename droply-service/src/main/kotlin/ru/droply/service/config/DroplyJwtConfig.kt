package ru.droply.service.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import ru.droply.service.util.PemUtils

private const val JWT_SIGN_ALGORITHM: String = "EC"

@Profile("!test")
@Configuration
class DroplyJwtConfig {
    @Value("\${droply.security.keys.public}")
    private lateinit var publicKeyLocation: String

    @Value("\${droply.security.keys.private}")
    private lateinit var privateKeyLocation: String

    @Value("\${droply.security.issuer}")
    private lateinit var issuer: String

    interface JWTIssuer {
        fun issue(payload: Map<String, Any?>): String
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
            override fun issue(payload: Map<String, Any?>) = JWT.create()
                .withIssuer(issuer)
                .withPayload(payload)
                .sign(algorithm())
        }
    }
}
