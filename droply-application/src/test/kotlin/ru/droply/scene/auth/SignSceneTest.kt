package ru.droply.scene.auth

import java.security.MessageDigest
import java.util.Random
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.data.entity.DroplyUser
import ru.droply.scenes.endpoint.auth.sign.SignScene
import ru.droply.scenes.endpoint.auth.sign.SignSceneInDto
import ru.droply.scenes.endpoint.auth.sign.SignSceneOutDto
import ru.droply.service.DroplyUserService
import ru.droply.service.extensions.auth
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals

class SignSceneTest : DroplyTest() {
    @Autowired
    private lateinit var signScene: SignScene

    companion object {
        private const val SAMPLE_PASS = "samplePassword"
        private val SHUFFLED_PASS = SAMPLE_PASS.toList().shuffled(Random(42)).joinToString()
    }

    private fun mockServiceAndUser(password: String? = SAMPLE_PASS): Pair<DroplyUserService, DroplyUser> {
        val digest = MessageDigest.getInstance("SHA-256")
        val passwordHash = password?.let { String(Hex.encode(digest.digest(it.encodeToByteArray()))) }

        val misterX = DroplyUser("X", "x@grandson.us", passwordHash = passwordHash)
        val userService = mock<DroplyUserService>()
        whenever(userService.findByEmail(any())).thenReturn(misterX)

        return userService to misterX
    }

    @Test
    fun `sign in correct password success`() {
        val (userService, misterX) = mockServiceAndUser()
        injectValue(signScene, "userService", userService) {
            // Signing in using the correct password
            socketIncoming(makeRequest("auth/sign", SignSceneInDto(misterX.email, SAMPLE_PASS))) {
                val response = assertReceive<SignSceneOutDto>(it)
                assert(response.success) { "Failed response: $response" }
                assertEquals("Authed in X", response.message)
            }
        }
    }

    @Test
    fun `sign in incorrect password failure`() {
        val (userService, misterX) = mockServiceAndUser()
        injectValue(signScene, "userService", userService) {
            // Trying to sign in using an incorrect password
            socketIncoming(makeRequest("auth/sign", SignSceneInDto(misterX.email, SHUFFLED_PASS))) {
                val response = assertReceive<SignSceneOutDto>(it)
                assert(!response.success) { "Unexpectedly successful response: $response" }
                assertEquals("Invalid credentials", response.message)
            }
        }
    }

    @Test
    fun `sign in googled authed user failure`() {
        val (userService, misterX) = mockServiceAndUser(password = null)
        injectValue(signScene, "userService", userService) {
            // Trying to sign in for a user authed via Google
            socketIncoming(makeRequest("auth/sign", SignSceneInDto(misterX.email, SAMPLE_PASS))) {
                val response = assertReceive<SignSceneOutDto>(it)
                assert(!response.success) { "Unexpectedly successful response: $response" }
                assertEquals("Invalid credentials", response.message)
            }
        }
    }

    @Test
    fun `sign up success`() {
        socketIncoming(makeRequest("auth/sign", SignSceneInDto("sample@user.net", SAMPLE_PASS))) {
            val response = assertReceive<SignSceneOutDto>(it)
            assert(response.success) { "Failed response: $response" }
            context.auth = null
        }
        socketIncoming(makeRequest("auth/sign", SignSceneInDto("sample@user.net", SAMPLE_PASS))) {
            val response = assertReceive<SignSceneOutDto>(it)
            assert(response.success) { "Failed response: $response" }
            assertEquals("Authed in sample", response.message)
        }
    }
    @Test
    fun `sign up twice failure`() {
        socketIncoming(makeRequest("auth/sign", SignSceneInDto("sample@user.net", SAMPLE_PASS))) {
            val response = assertReceive<SignSceneOutDto>(it)
            assert(response.success) { "Failed response: $response" }
        }
        socketIncoming(makeRequest("auth/sign", SignSceneInDto("sample@user.net", SAMPLE_PASS))) {
            val response = assertReceive<SignSceneOutDto>(it)
            assert(!response.success) { "Unexpectedly successful response: $response" }
            assertEquals("You are already logged in", response.message)
        }
    }
}
