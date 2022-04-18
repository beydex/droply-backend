package ru.droply.data.common.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class DroplyFileDto(val name: String, val size: Long)
