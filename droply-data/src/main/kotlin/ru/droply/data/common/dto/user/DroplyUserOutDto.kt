package ru.droply.data.common.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserOutDto(val name: String, val email: String, val urid: Int? = null, val avatarUrl: String? = null)
