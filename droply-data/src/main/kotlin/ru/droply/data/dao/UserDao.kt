package ru.droply.data.dao

import ru.droply.data.dao.base.BaseDao
import ru.droply.data.entity.DroplyUser

interface UserDao : BaseDao<Long, DroplyUser> {
    fun findByEmail(email: String): DroplyUser?
}
