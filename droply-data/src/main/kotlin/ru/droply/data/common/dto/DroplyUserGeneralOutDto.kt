package ru.droply.data.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserGeneralOutDto(val name: String, val email: String, val avatarUrl: String? = null)
