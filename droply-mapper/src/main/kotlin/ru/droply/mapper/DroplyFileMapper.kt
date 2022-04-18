package ru.droply.mapper

import org.mapstruct.Mapper
import ru.droply.data.common.dto.request.DroplyFileDto
import ru.droply.data.entity.DroplyFile

@Mapper(componentModel = "spring")
interface DroplyFileMapper {
    fun map(file: DroplyFile): DroplyFileDto

    fun map(fileDto: DroplyFileDto): DroplyFile

    fun map(fileDtos: Set<DroplyFileDto>): Set<DroplyFile>
}
