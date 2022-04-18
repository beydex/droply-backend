package ru.droply.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import ru.droply.data.common.dto.contact.ContactEntryOutDto
import ru.droply.data.entity.DroplyContact

@Mapper(componentModel = "spring", uses = [DroplyUserMapper::class])
interface DroplyContactMapper {
    @Mapping(target = "user", source = "contact")
    fun map(contact: DroplyContact): ContactEntryOutDto
}
