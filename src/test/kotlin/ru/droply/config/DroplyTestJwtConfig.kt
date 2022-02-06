package ru.droply.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class DroplyTestJwtConfig {
    @Bean
    fun algorithm(): Algorithm = Algorithm.HMAC256("somebody told me")

    @Bean
    fun jwtVerifier(): JWTVerifier = JWT.require(algorithm()).build()

    @Bean
    fun jwtIssuer(): DroplyJwtConfig.JWTIssuer = object : DroplyJwtConfig.JWTIssuer {
        override fun issue(payload: Map<String, Any?>) = JWT.create().withPayload(payload).sign(algorithm())
    }
}
