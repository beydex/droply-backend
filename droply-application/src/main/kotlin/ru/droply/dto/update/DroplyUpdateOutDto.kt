package ru.droply.dto.update

import kotlinx.serialization.Serializable

@Serializable(with = KDroplyUpdateOutDtoSerializer::class)
data class DroplyUpdateOutDto<out T>(val content: T, val type: DroplyUpdateType)
