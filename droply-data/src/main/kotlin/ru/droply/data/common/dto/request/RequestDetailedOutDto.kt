package ru.droply.data.common.dto.request

import kotlinx.serialization.Serializable
import ru.droply.data.common.dto.user.DroplyUserGeneralOutDto
import ru.droply.data.serializer.KZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class RequestDetailedOutDto(
    val requestId: Long,
    val sender: DroplyUserGeneralOutDto,
    val receiver: DroplyUserGeneralOutDto,
    val offer: String,

    val files: Set<DroplyFileDto> = mutableSetOf(),

    @Serializable(with = KZonedDateTimeSerializer::class)
    val creationTime: ZonedDateTime
)
