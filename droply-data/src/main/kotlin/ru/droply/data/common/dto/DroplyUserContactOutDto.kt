package ru.droply.data.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class DroplyUserContactOutDto(val id: Long, val name: String, val avatarUrl: String? = null)
