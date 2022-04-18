package ru.droply.data.common.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestSignalDto(
    val requestId: Long,
    val content: String
)
