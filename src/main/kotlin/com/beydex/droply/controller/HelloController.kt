package com.beydex.droply.controller

import com.beydex.droply.dto.Message
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class HelloController : AbstractDroplyController() {
    @MessageMapping("hello")
    fun connect(message: Message): Message {
        return Message("Hello, World")
    }
}