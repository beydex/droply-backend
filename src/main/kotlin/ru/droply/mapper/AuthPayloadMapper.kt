package ru.droply.mapper

import org.mapstruct.Mapper
import ru.droply.feature.context.auth.Auth
import ru.droply.feature.context.auth.AuthPayload

@Mapper(uses = [DroplyUserMapper::class], componentModel = "spring")
interface AuthPayloadMapper {
    fun map(auth: Auth): AuthPayload
}