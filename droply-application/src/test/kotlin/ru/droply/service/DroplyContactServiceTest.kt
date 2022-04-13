package ru.droply.service

import java.time.ZonedDateTime
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import ru.droply.data.entity.DroplyContact
import ru.droply.test.DroplyTest
import ru.droply.test.makeContact
import ru.droply.test.makeUser
import kotlin.test.assertEquals

@Transactional
class DroplyContactServiceTest : DroplyTest() {
    @Test
    fun `save contact success`() {
        val owner = makeUser()
        val contacted = makeUser(email = "contacted@droply.ru")

        val lastSuccessRequestDate = ZonedDateTime.now()
        val contact = DroplyContact(owner, contacted, lastSuccessRequestDate)

        val savedContact = contactService.save(contact)

        assertEquals(owner, savedContact.owner)
        assertEquals(lastSuccessRequestDate, savedContact.lastSuccessRequestDate)
    }

    @Test
    fun `fetch contacts success`() {
        val user = makeUser()
        val contacts = listOf(
            makeContact(makeUser(email = "me@droply.ru", username = "me")),
            makeContact(makeUser(email = "alsoMe@droply.ru", username = "alsoMe")),
            makeContact(makeUser(email = "nobody@droply.ru", username = "nobody"))
        ).also(user.contacts::addAll)

        val received = user.contacts
        assert(received.containsAll(contacts))
        assert(contacts.containsAll(received))
    }

    @Test
    fun `remove contact then fetch success`() {
        val user = makeUser()
        val contacts = mutableListOf(
            makeContact(makeUser(email = "me@droply.ru", username = "me")),
            makeContact(makeUser(email = "alsoMe@droply.ru", username = "alsoMe")),
            makeContact(makeUser(email = "nobody@droply.ru", username = "nobody"))
        ).also(user.contacts::addAll)

        val wiped = contacts[0]
        user.contacts.remove(wiped)
        contacts.remove(wiped)

        val expected = contacts.map(DroplyContact::id)
        val actual = user.contacts.map(DroplyContact::id)

        assert(expected.containsAll(actual))
        assert(actual.containsAll(expected))
    }
}