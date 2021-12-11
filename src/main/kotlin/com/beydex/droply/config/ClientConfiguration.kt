package com.beydex.droply.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration


@Configuration
@EnableReactiveMongoRepositories
@EnableReactiveMethodSecurity
@EnableRSocketSecurity
class ClientConfiguration : AbstractReactiveMongoConfiguration() {
    @Bean
    fun getRSocketRequester(): RSocketRequester {
        val builder = RSocketRequester.builder()
        return builder.rsocketConnector { it.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))) }
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
            .tcp("localhost", 7000)
    }

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create()
    }

    override fun getDatabaseName(): String {
        return "reactive"
    }

    @Bean
    fun messageHandler(strategies: RSocketStrategies?): RSocketMessageHandler {
        val handler = RSocketMessageHandler()
        handler.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
        handler.rSocketStrategies = strategies!!
        return handler
    }

    @Bean
    fun authorization(security: RSocketSecurity): PayloadSocketAcceptorInterceptor {
        return security
            .authorizePayload { authorizeSpec: RSocketSecurity.AuthorizePayloadsSpec ->
                authorizeSpec
                    .route("/authed").authenticated()
                    .anyExchange().permitAll()
            }
            .jwt(Customizer.withDefaults())
            .build()
    }
}