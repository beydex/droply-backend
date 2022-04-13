package ru.droply.data.dao

import org.springframework.data.jpa.repository.Query
import ru.droply.data.dao.base.BaseDao
import ru.droply.data.entity.DroplyContact

interface ContactDao : BaseDao<Long, DroplyContact> {
    @Query(
        """
        SELECT dc
        FROM DroplyContact dc
        WHERE dc.owner.id = :source AND dc.contact.id = :target
        """
    )
    fun getContact(source: Long, target: Long): DroplyContact?
}
