package ru.droply.data.common.dto.contact

import kotlinx.serialization.Serializable
import ru.droply.data.common.dto.user.DroplyUserContactOutDto
import ru.droply.data.serializer.KZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class ContactEntryOutDto(
    val user: DroplyUserContactOutDto,

    @Serializable(KZonedDateTimeSerializer::class)
    val lastSuccessRequestDate: ZonedDateTime
)
