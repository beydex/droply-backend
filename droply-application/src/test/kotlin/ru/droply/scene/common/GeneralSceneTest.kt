package ru.droply.scene.common

import org.junit.jupiter.api.Test
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeRequest
import ru.droply.test.socketIncoming
import kotlin.test.assertEquals

class GeneralSceneTest : DroplyTest() {
    companion object {
        const val UNKNOWN_SCENE_PATH = "MSOKF&(*DVDHJK@L@BNCDSKLJTFS*(PHDXCPSLK:@#__@+#_+"
    }

    @Test
    fun `request unknown scene then fail with not found`() {
        socketIncoming(makeRequest(UNKNOWN_SCENE_PATH, "sample" to "data")) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.NOT_FOUND, code)
            }
        }
    }
}