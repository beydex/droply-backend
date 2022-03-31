package ru.droply.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.util.*
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import ru.droply.service.config.DroplyJwtConfig

@TestConfiguration
class DroplyTestJwtConfig {
    @Bean
    fun algorithm(): Algorithm = Algorithm.HMAC256("somebody told me you")

    @Bean
    fun jwtVerifier(): JWTVerifier = JWT.require(algorithm()).build()

    @Bean
    fun jwtIssuer(): DroplyJwtConfig.JWTIssuer = object : DroplyJwtConfig.JWTIssuer {
        override fun issue(payload: Map<String, Any?>, expiresAt: Instant) =
            JWT.create()
                .withPayload(payload)
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm())
    }
}
