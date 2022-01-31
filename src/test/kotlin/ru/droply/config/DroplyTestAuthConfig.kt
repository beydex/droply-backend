package ru.droply.config

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class DroplyTestAuthConfig {
    @Bean
    fun googleIdTokenVerifier(): GoogleIdTokenVerifier =
        GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory()).build()
}