package ru.droply.data.common.auth

import kotlinx.serialization.Serializable
import ru.droply.data.common.dto.user.DroplyUserOutDto

@Serializable
data class AuthPayload(val provider: AuthProvider, val user: DroplyUserOutDto)
