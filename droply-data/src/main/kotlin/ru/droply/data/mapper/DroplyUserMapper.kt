package ru.droply.data.mapper

import org.mapstruct.Mapper
import ru.droply.data.entity.DroplyUser
import ru.droply.dto.user.DroplyUserOutDto

@Mapper(componentModel = "spring")
interface DroplyUserMapper {
    fun map(user: DroplyUser): DroplyUserOutDto
}
