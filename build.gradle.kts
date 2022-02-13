val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    kotlin("kapt") version "1.3.72"
}

group = "ru.droply"
version = "0.0.1"

repositories {
    mavenCentral()
    google()
}

/**
 * Task for automatic key generation
 */
tasks.register<Exec>("genkey") {
    val keysDirectory = file("keys")
    if (keysDirectory.exists() && !project.hasProperty("force-genkey")) {
        println("Keys already there do you want to regenerate them? [Y/*]")

        if (readLine() != "Y") {
            commandLine("echo", "ok")
            return@register
        }
    }

    // Remove previous keys
    keysDirectory.delete()

    if (!keysDirectory.exists()) {
        println("Making keys directory: $keysDirectory")
        keysDirectory.mkdir()
    }

    // Generate private key (not yet PKCS8)
    exec {
        commandLine(
            "openssl", "ecparam",
            "-genkey",
            "-name", "prime256v1",
            "-noout",
            "-out", "keys/private.raw.key"
        )
    }

    // Generate public key (not yet PKCS8)
    exec {
        commandLine(
            "openssl", "ec",
            "-in", "keys/private.raw.key",
            "-pubout",
            "-out", "keys/public.pem"
        )
    }

    // Convert private key to PKCS8
    exec {
        commandLine(
            "openssl", "pkcs8",
            "-topk8",
            "-inform", "PEM",
            "-outform", "PEM",
            "-in", "keys/private.raw.key",
            "-out", "keys/private.pem",
            "-nocrypt"
        )
    }

    commandLine("rm", "keys/private.raw.key")
    println("Keys have been successfully generated")
}
