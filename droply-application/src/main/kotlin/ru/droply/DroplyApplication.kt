package ru.droply

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import ru.droply.etc.makeBanner
import kotlin.properties.Delegates

private lateinit var droplyHost: String
private var droplyPort by Delegates.notNull<Int>()

@SpringBootApplication
class DroplyApplication : CommandLineRunner {
    // Host for Ktor WebSocket server
    @Value("\${droply.ktor.host}")
    private var springHost: String = "0.0.0.0"

    // Port for Ktor WebSocket server
    @Value("\${droply.ktor.port}")
    private var springPort = 8081

    @Autowired
    private lateinit var environment: Environment

    override fun run(vararg args: String?) {
        droplyHost = springHost
        droplyPort = springPort
        println(makeBanner(environment))
    }
}

fun main(args: Array<String>) {
    runApplication<DroplyApplication>(*args) { setBannerMode(Banner.Mode.OFF) }
    embeddedServer(Netty, host = droplyHost, port = droplyPort) { configureSockets() }
        .start(wait = true)
}
