package ru.droply.scene.auth.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.scene.auth.WhoamiOutDto
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals


class GoogleAuthSceneTest : DroplyTest() {
    @Autowired
    private lateinit var googleAuthScene: GoogleAuthScene

    @Test
    fun `call google auth valid token with name success`() {
        feignGoogleAuth("testcall@droply.ru") {
            socketIncoming(makeRequest("auth/google", GoogleAuthInDto("<mocked>"))) {
                assertReceive<GoogleAuthOutDto>(it).apply {
                    assert(success) { "Failed response $this" }
                    assertEquals("Welcome to Droply, testcall", message)
                }
            }
        }
    }

    @Test
    fun `call google auth valid token without name success`() {
        feignGoogleAuth("me@theseems.ru",) {
            socketIncoming(makeRequest("auth/google", GoogleAuthInDto("<mocked>"))) {
                assertReceive<GoogleAuthOutDto>(it).apply {
                    assert(success) { "Failed response $this" }
                    assertEquals("Welcome to Droply, me", message)
                }
            }
        }
    }

    @Test
    fun `call google auth valid token then call whoami success`() {
        feignGoogleAuth("SecurityControl@theseems.ru") {
            socketIncoming(makeRequest("auth/google", GoogleAuthInDto("<mocked>"))) {
                assertReceive<GoogleAuthOutDto>(it).apply {
                    assert(success) { "Failed response $this" }
                    assertEquals("Welcome to Droply, SecurityControl", message)
                }
            }

            socketIncoming(makeRequest("auth/whoami")) {
                val response = assertReceive<WhoamiOutDto>(it)
                assert(response.authenticated)

                val user = response.user!!
                assertEquals("SecurityControl", user.name)
                assertEquals("SecurityControl@theseems.ru", user.email)
            }
        }
    }

    private fun feignGoogleAuth(email: String, block: Runnable) {
        val payload = mock<GoogleIdToken.Payload>()

        whenever(payload.email).thenReturn(email)
        whenever(payload.emailVerified).thenReturn(true)

        val token = mock<GoogleIdToken> {
            on { it.payload }.then { payload }
        }

        val tokenVerifier = mock<GoogleIdTokenVerifier> {
            on { it.verify(any<String>()) }.then { token }
        }

        injectValue(googleAuthScene, "tokenVerifier", tokenVerifier, block)
    }
}