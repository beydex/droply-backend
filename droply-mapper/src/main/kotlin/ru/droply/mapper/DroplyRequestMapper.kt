package ru.droply.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import ru.droply.data.common.dto.request.RequestDetailedOutDto
import ru.droply.data.common.dto.request.RequestOutDto
import ru.droply.data.entity.DroplyRequest

@Mapper(componentModel = "spring")
interface DroplyRequestMapper {
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "requestId", source = "id")
    fun map(contact: DroplyRequest): RequestOutDto

    @Mapping(target = "requestId", source = "id")
    fun mapDetailed(contact: DroplyRequest): RequestDetailedOutDto
}
