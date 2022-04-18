package ru.droply.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Test
import ru.droply.dto.update.DroplyUpdateOutDto
import ru.droply.dto.update.DroplyUpdateType
import ru.droply.test.DroplyTest
import kotlin.test.assertEquals

class DroplyUpdateOutDtoSerializationTest : DroplyTest() {
    @Test
    fun `deserialize update success`() {
        testDeserialize(
            buildJsonObject {
                put("myPayload", JsonPrimitive(123))
            }
        )
    }

    @Test
    fun `serialize update success`() {
        testSerialize(
            buildJsonObject {
                put("myPayload", JsonPrimitive(123))
            }
        )
    }

    private inline fun <reified T : Any> testDeserialize(source: T) {
        val serialized = Json.decodeFromString<DroplyUpdateOutDto<T>>(
            Json.encodeToString(
                buildJsonObject {
                    put(
                        "update",
                        buildJsonObject {
                            put("content", Json.encodeToJsonElement(source))
                            put("type", Json.encodeToJsonElement(DroplyUpdateType.UNKNOWN))
                        }
                    )
                }
            )
        )

        assertEquals(DroplyUpdateType.UNKNOWN, serialized.type)
        assertEquals(source, serialized.content)
    }

    private inline fun <reified T : Any> testSerialize(source: T) {
        val deserialized =
            Json.encodeToJsonElement(DroplyUpdateOutDto(content = source, type = DroplyUpdateType.UNKNOWN))

        assertEquals(
            buildJsonObject {
                put(
                    "update",
                    buildJsonObject {
                        put("content", Json.encodeToJsonElement(source))
                        put("type", Json.encodeToJsonElement(DroplyUpdateType.UNKNOWN))
                    }
                )
            },
            deserialized
        )
    }
}
