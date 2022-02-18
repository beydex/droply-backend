package ru.droply.data.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserOutDto(val name: String, val email: String, val avatarUrl: String? = null)
