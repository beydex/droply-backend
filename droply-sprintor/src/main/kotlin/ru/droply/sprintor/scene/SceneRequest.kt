package ru.droply.sprintor.scene

import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import ru.droply.data.serializer.UUIDSerializer

@Serializable
data class SceneRequest(
    val path: String,
    val request: JsonObject? = JsonObject(mapOf()),
    @Serializable(with = UUIDSerializer::class) val nonce: UUID
)
