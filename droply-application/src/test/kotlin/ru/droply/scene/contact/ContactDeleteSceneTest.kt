package ru.droply.scene.contact

import org.junit.jupiter.api.Test
import ru.droply.scenes.endpoint.contact.ContactDeleteInDto
import ru.droply.scenes.endpoint.contact.ContactListOutDto
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

class ContactDeleteSceneTest : DroplyTest() {
    @Test
    fun `call scene with no auth failure`() {
        socketIncoming(makeRequest("contact/delete", ContactDeleteInDto(id = 0))) {
            assertReceive<DroplyErrorResponse>(it).apply {
                assert(!success)
                assertEquals(DroplyErrorCode.UNAUTHORIZED, code)
            }
        }
    }

    @Test
    fun `call scene with contacts then delete existent success`() {
        val alice = makeUser(email = "alice@droply.ru")
        val bob = makeUser(email = "bob@droply.ru")

        val me = userService.findByIdAndFetchContacts(makeUser(email = "me@droply.ru").id!!)!!
        me.contacts.add(makeContact(me, alice))
        me.contacts.add(makeContact(me, bob))

        useAuthUser(userService.save(me)) {
            socketIncoming(makeRequest("contact/delete", ContactDeleteInDto(id = alice.id!!))) {
                assertReceive<ContactListOutDto>(it).apply {
                    assertEquals(1, entries.size)
                }
            }
        }
    }

    @Test
    fun `call scene with contacts then non-existent user failure`() {
        val alice = makeUser(email = "alice@droply.ru")

        val me = userService.findByIdAndFetchContacts(makeUser(email = "me@droply.ru").id!!)!!
        me.contacts.add(makeContact(me, alice))

        useAuthUser(userService.save(me)) {
            socketIncoming(makeRequest("contact/delete", ContactDeleteInDto(id = -1))) {
                assertReceive<DroplyErrorResponse>(it).apply {
                    assert(!success)
                    assertEquals(DroplyErrorCode.NOT_FOUND, code)
                }
            }
        }
    }

    @Test
    fun `call scene with contacts then delete non-listed person failure`() {
        val alice = makeUser(email = "alice@droply.ru")
        val me = makeUser(email = "me@droply.ru")
        me.contacts.add(makeContact(me, alice))

        val eve = makeUser(email = "eve@droply.ru")

        useAuthUser(me) {
            socketIncoming(makeRequest("contact/delete", ContactDeleteInDto(id = eve.id!!))) {
                assertReceive<DroplyErrorResponse>(it).apply {
                    assert(!success)
                    assertEquals(DroplyErrorCode.NOT_FOUND, code)
                }
            }
        }
    }
}