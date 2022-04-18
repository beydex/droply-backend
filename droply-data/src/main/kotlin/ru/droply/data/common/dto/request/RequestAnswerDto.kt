package ru.droply.data.common.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestAnswerDto(
    val requestId: Long,
    val accept: Boolean,
    val answer: String? = null
)
