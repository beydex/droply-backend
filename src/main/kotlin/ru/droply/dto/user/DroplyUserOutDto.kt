package ru.droply.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserOutDto(val name: String, val email: String)
