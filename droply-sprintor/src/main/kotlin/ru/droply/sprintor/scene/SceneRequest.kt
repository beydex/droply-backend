package ru.droply.sprintor.scene

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SceneRequest(val path: String, val request: JsonObject? = JsonObject(mapOf()))
