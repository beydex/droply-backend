package ru.droply.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.droply.data.dao.UserDao
import ru.droply.data.entity.DroplyUser
import ru.droply.sprintor.context.Context
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException

@Service
class DroplyUserService {
    @Autowired
    private lateinit var userDao: UserDao

    @Transactional
    fun makeUser(name: String, email: String, avatarUrl: String? = null): DroplyUser =
        userDao.save(DroplyUser(name, email, avatarUrl))

    @Transactional
    fun save(user: DroplyUser): DroplyUser = userDao.save(user)

    @Transactional(readOnly = true)
    fun findByEmail(email: String) = userDao.findByEmail(email)

    @Transactional(readOnly = true)
    fun findById(id: Long): DroplyUser? = userDao.findById(id).orElse(null)

    @Transactional(readOnly = true)
    fun findByUrid(urid: Int) = userDao.findByUrid(urid)

    @Transactional
    fun removeUserByEmail(email: String) {
        val user = userDao.findByEmail(email)
        if (user != null) {
            userDao.delete(user)
        }
    }

    @Transactional
    fun updateUserUrid(user: DroplyUser): Int {
        userDao.updateUserUrid(user.id!!)
        return userDao.findByEmail(user.email)!!.urid!!
    }

    @Transactional(readOnly = true)
    fun requireUser(context: Context): DroplyUser {
        val auth = context.auth
        if (auth == null || auth.user.id == null) {
            throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)
        }

        val user = userDao.findById(auth.user.id!!).orElse(null)
        if (user == null) {
            context.auth = null
            throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)
        }

        return user
    }

    @Transactional(readOnly = true)
    fun fetchUser(context: Context): DroplyUser? {
        val auth = context.auth
        if (auth == null || auth.user.id == null) {
            return null
        }

        val user = userDao.findById(auth.user.id!!).orElse(null)
        if (user == null) {
            context.auth = null
            return null
        }

        return user
    }
}
