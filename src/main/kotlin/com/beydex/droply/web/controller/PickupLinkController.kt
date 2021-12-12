package com.beydex.droply.web.controller

import com.beydex.droply.util.AbstractDroplyController
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

@Controller
class PickupLinkController : AbstractDroplyController() {
    @MessageMapping("auth/thru")
    fun getThru(token: String): Flux<Any> {
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory()).build()
        val idToken: GoogleIdToken = verifier.verify(token) ?: throw IllegalStateException("Incorrect token provided")
        val payload = idToken.payload

        return Flux.fromIterable(
            listOf(
                payload.email ?: "no email",
                payload.keys.toString(),
                payload["picture"] ?: "no picture",
                payload["locale"] ?: "no locale",
                payload["family_name"] ?: "no family name"
            )
        ).delayElements(Duration.ofSeconds(2))
    }
}