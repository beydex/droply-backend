package ru.droply.etc

import org.springframework.context.ApplicationEvent
import ru.droply.service.DroplyRequestService
import ru.droply.service.DroplyUserService
import ru.droply.sprintor.connector.DroplyLocator
import ru.droply.sprintor.event.DroplyEvent
import ru.droply.sprintor.event.UserLoginEvent
import ru.droply.sprintor.event.UserLogoutEvent
import ru.droply.sprintor.event.UserRequestAnswerEvent
import ru.droply.sprintor.event.UserRequestSendEvent
import ru.droply.sprintor.event.UserRequestSignalEvent
import ru.droply.sprintor.messaging.messages.EventMessage
import ru.droply.sprintor.messaging.messages.RequestSendMessage
import ru.droply.sprintor.messaging.messages.RequestSignalMessage
import ru.droply.sprintor.messaging.messages.UserLoginMessage
import ru.droply.sprintor.messaging.messages.UserLogoutMessage
import ru.droply.sprintor.messaging.messages.UserRequestAnswerMessage
import ru.droply.sprintor.spring.autowired

private val droplyUserService: DroplyUserService by autowired()
private val droplyRequestService: DroplyRequestService by autowired()
private val droplyLocator: DroplyLocator by autowired()

fun UserLoginEvent.toMessage() = UserLoginMessage(user.id!!)
fun UserLogoutEvent.toMessage() = UserLogoutMessage(user.id!!)
fun UserRequestSendEvent.toMessage() = RequestSendMessage(request.id!!)
fun UserRequestAnswerEvent.toMessage() = UserRequestAnswerMessage(request.id!!, issuer.id!!, accept, answer)
fun UserRequestSignalEvent.toMessage() = RequestSignalMessage(request.id!!, sender.id!!, content)

fun UserLoginMessage.toEvent() =
    droplyLocator.lookupUser(userId)?.let { UserLoginEvent(droplyUserService.findById(userId)!!, it) }

fun UserLogoutMessage.toEvent() =
    UserLogoutEvent(droplyUserService.findById(userId)!!)

fun RequestSendMessage.toEvent() = UserRequestSendEvent(droplyRequestService.fetchRequest(requestId)!!)
fun RequestSignalMessage.toEvent() = UserRequestSignalEvent(
    droplyRequestService.findRequest(requestId)!!,
    droplyUserService.findById(senderId)!!,
    content
)

fun UserRequestAnswerMessage.toEvent() = UserRequestAnswerEvent(
    droplyRequestService.fetchRequest(requestId)!!,
    droplyUserService.findById(issuerId)!!,
    accept,
    answer
)

fun DroplyEvent.toMessage() = when (this) {
    is UserLoginEvent -> toMessage()
    is UserLogoutEvent -> toMessage()
    is UserRequestAnswerEvent -> toMessage()
    is UserRequestSendEvent -> toMessage()
    is UserRequestSignalEvent -> toMessage()
    else -> throw IllegalArgumentException("No message available")
}

fun EventMessage.toEvent(external: Boolean = false): ApplicationEvent? {
    val event = when (this) {
        is RequestSendMessage -> toEvent()
        is RequestSignalMessage -> toEvent()
        is UserLoginMessage -> toEvent()
        is UserLogoutMessage -> toEvent()
        is UserRequestAnswerMessage -> toEvent()
        else -> throw IllegalArgumentException("No event available")
    }
    if (event != null && external) {
        event.makeExternal()
    }
    return event
}
