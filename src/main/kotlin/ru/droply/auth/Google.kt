package ru.droply.auth

import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.droply.dao.UserDao
import ru.droply.entity.DroplyUser
import ru.droply.feature.context.ConnectionPool
import ru.droply.feature.ext.retCreate
import ru.droply.feature.ext.retrieveText
import ru.droply.feature.ext.sendJson
import ru.droply.feature.spring.autowired

private val userDao: UserDao by autowired()
private val connectionPool: ConnectionPool by autowired()


fun Route.authRouter() {
    webSocket("/auth/social/google") {
        val context = connectionPool[this]

        class Sample {
            var sample: Long = 0
        }

        incoming.retrieveText {
            val count = withContext(Dispatchers.IO) { userDao.count() }
            val sample = context.retCreate("sample") { Sample() }

            sample.sample += readText().length + count
            outgoing.sendJson(sample)
        }
    }

    webSocket("/list") {
        outgoing.sendJson(withContext(Dispatchers.IO) {
            userDao.findAll()
        })
    }

    webSocket("/sample") {
        incoming.retrieveText {
            val userName = readText()
            outgoing.sendJson(
                withContext(Dispatchers.IO) {
                    userDao.save(
                        DroplyUser(
                            userName,
                            "$userName@droply.ru"
                        )
                    )
                }
            )
        }
    }
}