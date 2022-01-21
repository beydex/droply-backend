package ru.droply.dao

import ru.droply.dao.base.BaseDao
import ru.droply.entity.DroplyUser

interface UserDao : BaseDao<Long, DroplyUser> {
    fun findByEmail(email: String): DroplyUser?
}