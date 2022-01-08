package ru.droply

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.droply.plugins.configureSockets

@SpringBootApplication
class DroplyApplication

fun main(args: Array<String>) {
    println("""
        
            ___                     _        
           /   \ _ __  ___   _ __  | | _   _ 
          / /\ /| '__|/ _ \ | '_ \ | || | | |
         / /_// | |  | (_) || |_) || || |_| |
        /___,'  |_|   \___/ | .__/ |_| \__, |
                            |_|        |___/ 

    """.trimIndent())

    runApplication<DroplyApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }

    embeddedServer(Netty, port = 8081, host = "0.0.0.0") { configureSockets() }
        .start(wait = true)
}
