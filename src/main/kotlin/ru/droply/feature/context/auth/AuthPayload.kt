package ru.droply.feature.context.auth

import kotlinx.serialization.Serializable
import ru.droply.dto.user.DroplyUserOutDto

@Serializable
data class AuthPayload(val provider: AuthProvider, val user: DroplyUserOutDto)