package ru.droply.mapper

import org.mapstruct.Mapper
import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthPayload

@Mapper(uses = [DroplyUserMapper::class], componentModel = "spring")
interface AuthPayloadMapper {
    fun map(auth: Auth): AuthPayload
}
