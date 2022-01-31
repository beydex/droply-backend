package ru.droply.mapper

import org.mapstruct.Mapper
import ru.droply.dto.user.DroplyUserOutDto
import ru.droply.entity.DroplyUser

@Mapper(componentModel = "spring")
interface DroplyUserMapper {
    fun map(user: DroplyUser): DroplyUserOutDto
}