package ru.droply.data.dao

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.droply.data.dao.base.BaseDao
import ru.droply.data.entity.DroplyRequest

interface RequestDao : BaseDao<Long, DroplyRequest> {
    @Query(
        """
        SELECT rq FROM DroplyRequest rq
        LEFT JOIN FETCH rq.files
        WHERE rq.id = :requestId
        """
    )
    fun fetchRequest(@Param("requestId") requestId: Long): DroplyRequest?

    @Modifying
    @Query(
        """
        UPDATE DroplyRequest rq
        SET rq.active = true 
        WHERE rq.id = :requestId
        """
    )
    fun setActive(@Param("requestId") requestId: Long)
}
