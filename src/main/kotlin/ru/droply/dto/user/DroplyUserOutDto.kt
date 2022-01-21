package ru.droply.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserOutDto(private val name: String, private val email: String)