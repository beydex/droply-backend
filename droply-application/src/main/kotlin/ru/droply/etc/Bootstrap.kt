package ru.droply.etc

import org.springframework.core.env.Environment
import org.springframework.core.env.get

const val DROPLY_BANNER = """
            ___                     _        
           /   \ _ __  ___   _ __  | | _   _ 
          / /\ /| '__|/ _ \ | '_ \ | || | | |
         / /_// | |  | (_) || |_) || || |_| |
        /___,'  |_|   \___/ | .__/ |_| \__, |
                            |_|        |___/ 
                            
        By Beydex Team :::: Running backend%s
"""

const val SPACE_FILLER = "::::::::::::::::::: "
const val PROFILES_ANNOUNCER = "\uD83D\uDCC4 Profiles: %s"

val PROFILES_DECORATIONS = mapOf(
    "test" to "\uD83E\uDDEA Test profile"
)

val RUN_DECORATIONS = mapOf(
    "local" to "\uD83D\uDCBB Local run",
    "real" to "\uD83D\uDEEC Real run"
)

fun makeBanner(environment: Environment): String {
    val activeProfiles = environment.activeProfiles
    val description = mutableListOf<String>()

    if (activeProfiles.isNotEmpty()) {
        description += PROFILES_ANNOUNCER.format(activeProfiles.toList().toString())
    }

    PROFILES_DECORATIONS.forEach { (name, decoration) ->
        if (activeProfiles.contains(name)) {
            description += decoration
        }
    }

    val runType = if (environment["droply.localRun"] == "true") "local" else "real"
    description += RUN_DECORATIONS[runType] ?: "UNKNOWN RUN"

    return DROPLY_BANNER.trimIndent().format(
        description
            .joinToString(prefix = "\n", separator = "\n") { "$SPACE_FILLER$it" }
            .removeSuffix("\n")
    )
}
