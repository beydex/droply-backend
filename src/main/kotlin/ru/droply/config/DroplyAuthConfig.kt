package ru.droply.config

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class DroplyAuthConfig(
    // Client ID for Google OAuth2
    @field:Value("\${droply.auth.google.clientId}")
    private val clientId: String?
) {
    private val logger: Logger = LogManager.getLogger(DroplyAuthConfig::class.java)

    @Bean
    fun googleIdTokenVerifier(): GoogleIdTokenVerifier {
        val builder = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        if (clientId != null) {
            builder.audience = listOf(clientId)
            logger.info("Google OAuth2 client id is provided.")
        } else {
            logger.warn("No client id provided. Google Auth will not prohibit tokens from other OAuth2 apps.")
        }

        return builder.build()
    }
}
