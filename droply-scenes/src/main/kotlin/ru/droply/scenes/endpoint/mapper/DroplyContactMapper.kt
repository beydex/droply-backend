package ru.droply.scenes.endpoint.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import ru.droply.data.entity.DroplyContact
import ru.droply.data.mapper.DroplyUserMapper
import ru.droply.scenes.endpoint.contact.ContactEntryOutDto

@Mapper(componentModel = "spring", uses = [DroplyUserMapper::class])
interface DroplyContactMapper {
    @Mapping(target = "user", source = "contact")
    fun map(contact: DroplyContact): ContactEntryOutDto
}