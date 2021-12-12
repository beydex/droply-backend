package com.beydex.droply.web.controller

import com.beydex.droply.web.dto.Message
import com.beydex.droply.entity.User
import com.beydex.droply.repository.UserRepository
import com.beydex.droply.util.AbstractDroplyController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono


@Controller
class HelloController : AbstractDroplyController() {
    @Autowired
    lateinit var userRepository: UserRepository


}