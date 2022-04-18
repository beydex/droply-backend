package ru.droply.data.dao

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.droply.data.dao.base.BaseDao
import ru.droply.data.entity.DroplyUser

interface UserDao : BaseDao<Long, DroplyUser> {
    fun findByEmail(email: String): DroplyUser?

    fun findByUrid(urid: Int): DroplyUser?

    @Query(
        """
        SELECT u
        FROM DroplyUser u
        LEFT JOIN FETCH u.contacts
        WHERE u.id = (:id)
        """
    )
    fun findByIdAndFetchContacts(@Param("id") id: Long): DroplyUser?

    @Query(
        """
        SELECT u
        FROM DroplyUser u
        LEFT JOIN FETCH u.incomingRequests
        WHERE u.id = (:id)
        """
    )
    fun findByIdAndFetchIncomingRequests(@Param("id") id: Long): DroplyUser?

    @Query(
        """
        SELECT u
        FROM DroplyUser u
        LEFT JOIN FETCH u.outgoingRequests
        WHERE u.id = (:id)
        """
    )
    fun findByIdAndFetchOutgoingRequests(@Param("id") id: Long): DroplyUser?

    @Query(
        """
        SELECT DISTINCT u
        FROM DroplyUser u
        LEFT JOIN FETCH u.incomingRequests
        LEFT JOIN FETCH u.outgoingRequests
        WHERE u.id = (:id)
        """
    )
    fun findByIdAndFetchRequests(@Param("id") id: Long): DroplyUser?

    @Modifying
    @Query(
        """
        LOCK TABLE droply_user_id_urid IN ACCESS EXCLUSIVE MODE;
        CALL make_random_code(:userId)
        """,
        nativeQuery = true
    )
    fun updateUserUrid(@Param("userId") userId: Long)
}
