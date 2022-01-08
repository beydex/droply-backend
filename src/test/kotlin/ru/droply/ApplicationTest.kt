package ru.droply

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.droply.dao.UserDao
import ru.droply.entity.DroplyUser
import kotlin.test.assertEquals

class ApplicationTest: DroplyTest() {
    @Autowired
    private lateinit var userDao: UserDao

    @Test
    fun contextLoads() {
        val user = userDao.save(DroplyUser(name = "TheSeems", email = "me@theseems.ru"))
        assertEquals("TheSeems", user.name)
        assertEquals("me@theseems.ru", user.email)
    }
}