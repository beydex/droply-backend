package ru.droply.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.droply.data.dao.RequestDao
import ru.droply.data.entity.DroplyFile
import ru.droply.data.entity.DroplyRequest
import ru.droply.data.entity.DroplyUser
import ru.droply.sprintor.event.UserRequestAnswerEvent
import ru.droply.sprintor.event.UserRequestSendEvent
import java.time.ZonedDateTime

@Service
class DroplyRequestService {

    @Autowired
    private lateinit var requestDao: RequestDao

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var userService: DroplyUserService

    @Transactional(readOnly = true)
    fun findRequest(requestId: Long): DroplyRequest? = requestDao.findById(requestId).orElse(null)

    @Transactional(readOnly = true)
    fun fetchRequest(requestId: Long): DroplyRequest? = requestDao.fetchRequest(requestId)

    @Transactional
    fun sendRequest(sender: DroplyUser, receiver: DroplyUser, offer: String, files: Set<DroplyFile>): DroplyRequest {
        val request = requestDao.save(DroplyRequest(sender, receiver, ZonedDateTime.now(), offer, files))

        applicationEventPublisher.publishEvent(UserRequestSendEvent(request))
        sender.outgoingRequests.add(request)
        receiver.incomingRequests.add(request)

        return request
    }

    @Transactional
    fun removeRequest(
        droplyRequest: DroplyRequest,
        issuer: DroplyUser,
        accept: Boolean = false,
        answer: String? = null
    ) {
        val sender = userService.findFetchOutgoingRequests(droplyRequest.sender.id!!)!!
        val receiver = userService.findFetchIncomingRequests(droplyRequest.receiver.id!!)!!

        applicationEventPublisher.publishEvent(UserRequestAnswerEvent(droplyRequest, issuer, accept, answer))

        sender.outgoingRequests.remove(droplyRequest)
        receiver.incomingRequests.remove(droplyRequest)
        requestDao.delete(droplyRequest)
    }

    @Transactional
    fun setActive(droplyRequest: DroplyRequest) {
        requestDao.setActive(droplyRequest.id!!)
    }
}
