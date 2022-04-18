package ru.droply.data.common.dto.request

import kotlinx.serialization.Serializable
import ru.droply.data.serializer.KZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class RequestOutDto(
    val requestId: Long,
    val senderId: Long,
    val receiverId: Long,
    val offer: String,

    val files: Set<DroplyFileDto> = mutableSetOf(),

    @Serializable(with = KZonedDateTimeSerializer::class)
    val creationTime: ZonedDateTime
)
