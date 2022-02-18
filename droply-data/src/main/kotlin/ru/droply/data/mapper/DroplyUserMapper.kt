package ru.droply.data.mapper

import org.mapstruct.Mapper
import ru.droply.data.common.dto.DroplyUserOutDto
import ru.droply.data.entity.DroplyUser

@Mapper(componentModel = "spring")
interface DroplyUserMapper {
    fun map(user: DroplyUser): DroplyUserOutDto
}
