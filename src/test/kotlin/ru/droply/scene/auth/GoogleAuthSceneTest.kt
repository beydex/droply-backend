package ru.droply.scene.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals

class GoogleAuthSceneTest : DroplyTest() {
    @MockBean
    private lateinit var idVerifier: GoogleIdTokenVerifier

    @Test
    fun `call google auth scene valid token with name success`() {
        mockGoogleAuth("testcall@droply.ru", "Beydex Team")
        socketIncoming(makeRequest("auth/google", GoogleAuthInDto("anything"))) {
            assertReceive<GoogleAuthOutDto>(it).apply {
                assert(success)
                assertEquals("Welcome to Droply, Beydex Team", message)
            }
        }
    }

    @Test
    fun `call google auth scene valid token without name success`() {
        mockGoogleAuth("me@theseems.ru")
        socketIncoming(makeRequest("auth/google", GoogleAuthInDto("anything"))) {
            assertReceive<GoogleAuthOutDto>(it).apply {
                assert(success)
                assertEquals("Welcome to Droply, me", message)
            }
        }
    }

    private fun mockGoogleAuth(mockEmail: String, mockName: String? = null) {
        Mockito.`when`(idVerifier.verify(Mockito.any<String>()))
            .then { mockGoogleToken(mockEmail, mockName) }
    }

    private fun mockGoogleToken(mockEmail: String, mockName: String? = null): GoogleIdToken {
        val token = Mockito.mock(GoogleIdToken::class.java)
        Mockito.`when`(token.payload).thenReturn(
            GoogleIdToken.Payload().apply {
                email = mockEmail
                emailVerified = true
                set("name", mockName)
            })

        return token
    }
}