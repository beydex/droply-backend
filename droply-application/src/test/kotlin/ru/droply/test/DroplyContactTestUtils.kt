package ru.droply.test

import ru.droply.data.entity.DroplyContact
import ru.droply.data.entity.DroplyUser
import java.time.ZonedDateTime

fun DroplyTest.makeContact(
    owner: DroplyUser = makeUser(),
    contacted: DroplyUser = makeUser(email = "contacted@droply.ru"),
    lastSuccessfulContact: ZonedDateTime = ZonedDateTime.now()
) = DroplyContact(owner, contacted, lastSuccessfulContact)
