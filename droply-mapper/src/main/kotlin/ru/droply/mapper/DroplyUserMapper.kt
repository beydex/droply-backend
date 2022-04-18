package ru.droply.mapper

import org.mapstruct.Mapper
import ru.droply.data.common.dto.user.DroplyUserContactOutDto
import ru.droply.data.common.dto.user.DroplyUserGeneralOutDto
import ru.droply.data.common.dto.user.DroplyUserOutDto
import ru.droply.data.entity.DroplyUser

@Mapper(componentModel = "spring")
interface DroplyUserMapper {
    fun map(user: DroplyUser): DroplyUserOutDto
    fun mapToGeneral(user: DroplyUser): DroplyUserGeneralOutDto
    fun mapToContact(user: DroplyUser): DroplyUserContactOutDto
}
