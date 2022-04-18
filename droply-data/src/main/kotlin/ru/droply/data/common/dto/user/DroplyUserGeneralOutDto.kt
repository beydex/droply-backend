package ru.droply.data.common.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserGeneralOutDto(val name: String, val urid: Int?, val avatarUrl: String? = null)
