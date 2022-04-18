package ru.droply.dto.update

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

class KDroplyUpdateOutDtoSerializer<T>(private val dataSerializer: KSerializer<T>) :
    KSerializer<DroplyUpdateOutDto<T>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DroplyUpdateOutDtoWrapper") {
        element(
            "update",
            buildClassSerialDescriptor("DroplyUpdateOutDto") {
                element("content", dataSerializer.descriptor)
                element<DroplyUpdateType>("type")
            }
        )
    }

    override fun deserialize(decoder: Decoder): DroplyUpdateOutDto<T> {
        require(decoder is JsonDecoder)

        val element = decoder.decodeJsonElement()
        require(element is JsonObject)
        require("update" in element)

        val update = element["update"]!!
        require(update is JsonObject)
        require("content" in update)
        require("type" in update)

        return DroplyUpdateOutDto(
            content = decoder.json.decodeFromJsonElement(dataSerializer, update["content"]!!),
            type = decoder.json.decodeFromJsonElement(update["type"]!!)
        )
    }

    override fun serialize(encoder: Encoder, value: DroplyUpdateOutDto<T>) {
        require(encoder is JsonEncoder)
        return encoder.encodeJsonElement(
            buildJsonObject {
                put(
                    "update",
                    buildJsonObject {
                        put("content", encoder.json.encodeToJsonElement(dataSerializer, value.content))
                        put("type", encoder.json.encodeToJsonElement(value.type))
                    }
                )
            }
        )
    }
}
