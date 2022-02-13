package ru.droply.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.droply.data.dao.UserDao
import ru.droply.data.entity.DroplyUser

@Service
class DroplyUserService {
    @Autowired
    private lateinit var userDao: UserDao

    @Transactional
    fun makeUser(name: String, email: String): DroplyUser = userDao.save(DroplyUser(name, email))

    @Transactional
    fun save(user: DroplyUser): DroplyUser = userDao.save(user)

    @Transactional(readOnly = true)
    fun findByEmail(email: String) = userDao.findByEmail(email)

    @Transactional(readOnly = true)
    fun findAll(): List<DroplyUser> = userDao.findAll()
}
