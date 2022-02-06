package ru.droply

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import ru.droply.feature.ktor.configureSockets
import kotlin.properties.Delegates

private lateinit var droplyHost: String
private var droplyPort by Delegates.notNull<Int>()
private val droplyBanner = """
            ___                     _        
           /   \ _ __  ___   _ __  | | _   _ 
          / /\ /| '__|/ _ \ | '_ \ | || | | |
         / /_// | |  | (_) || |_) || || |_| |
        /___,'  |_|   \___/ | .__/ |_| \__, |
                            |_|        |___/ 
                            
        By Beydex Team :::: Running backend%s
""".trimIndent()

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
        println(droplyBanner.trimIndent().format(formatSpecial(environment)))
    }
}

fun main(args: Array<String>) {
    runApplication<DroplyApplication>(*args) { setBannerMode(Banner.Mode.OFF) }
    embeddedServer(Netty, host = droplyHost, port = droplyPort) { configureSockets() }
        .start(wait = true)
}

private fun formatSpecial(environment: Environment): String {
    val description = mutableListOf<String>()

    val activeProfiles = environment.activeProfiles
    description += "\uD83D\uDCC4 Profiles: " + activeProfiles.toList()

    if (activeProfiles.contains("test")) {
        description += "\uD83E\uDDEA Test profile"
    }

    val localRun = environment["droply.localRun"] == "true"
    if (localRun) {
        description += "\uD83D\uDCBB Local run"
    }

    var result = ""
    if (description.isNotEmpty()) {
        result = description
            .joinToString(prefix = "\n", separator = "\n") { "::::::::::::::::::: $it" }
            .removeSuffix("\n")
    }

    return result
}
