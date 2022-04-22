package ru.droply.service

import java.time.ZonedDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.droply.data.dao.ContactDao
import ru.droply.data.entity.DroplyContact
import ru.droply.data.entity.DroplyUser

@Service
class DroplyContactService {
    @Autowired
    private lateinit var contactDao: ContactDao

    @Transactional
    fun save(contact: DroplyContact) = contactDao.save(contact)

    @Transactional
    fun remove(contact: DroplyContact) = contactDao.delete(contact)

    @Transactional(readOnly = true)
    fun findById(id: Long) = contactDao.findById(id)

    @Transactional(readOnly = true)
    fun getContact(source: DroplyUser, target: DroplyUser) = contactDao.getContact(source.id!!, target.id!!)

    @Transactional
    fun createOrUpdateContact(source: DroplyUser, target: DroplyUser) {
        val contact = contactDao.getContact(source.id!!, target.id!!)
        if (contact == null) {
            contactDao.save(
                DroplyContact(
                    owner = source,
                    contact = target,
                    lastSuccessRequestDate = ZonedDateTime.now()
                )
            )
        } else {
            contact.lastSuccessRequestDate = ZonedDateTime.now()
        }
    }
}
