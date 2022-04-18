package ru.droply.scene.contact

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import ru.droply.data.dao.UserDao
import ru.droply.data.entity.DroplyContact
import ru.droply.scenes.endpoint.contact.ContactListOutDto
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.DroplyErrorResponse
import ru.droply.test.DroplyTest
import ru.droply.test.assertReceive
import ru.droply.test.makeContact
import ru.droply.test.makeRequest
import ru.droply.test.makeUser
import ru.droply.test.socketIncoming
import ru.droply.test.useAuthUser
import kotlin.test.assertEquals

class ContactListSceneTest : DroplyTest() {
    @Autowired
    private lateinit var userDao: UserDao

    @Test
    fun `call scene with no auth failure`() {
        socketIncoming(makeRequest("contact/list")) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
            }
        }
    }

    @Test
    fun `call scene with single contact then check entries success`() {
        userService.deleteAll()

        val contacted = makeUser(username = "contact", email = "contact@droply.ru")
        val me = userService.findByIdAndFetchContacts(makeUser(username = "me", email = "me@droply.ru").id!!)!!

        me.contacts.forEach(contactService::remove)
        me.contacts.add(makeContact(me, contacted))

        useAuthUser(userService.save(me)) { user ->
            socketIncoming(makeRequest("contact/list")) {
                assertReceive<ContactListOutDto>(it).apply {
                    assert(success)
                    assertEquals(1, entries.size, "$entries")

                    assertEquals(
                        contactService
                            .findById(user.contacts.iterator().next().id!!)
                            .map(DroplyContact::lastSuccessRequestDate)
                            .get(),
                        entries[0].lastSuccessRequestDate
                    )

                    assertEquals("contact", entries[0].user.name)
                }
            }
        }
    }

    @Transactional
    fun DroplyUserService.deleteAll() {
        userDao.deleteAll()
    }
}
