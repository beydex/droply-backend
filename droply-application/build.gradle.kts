group = "ru.droply"
version = "0.0.1"

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")

    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.google.cloud.tools.jib") version "3.2.0"
}

repositories {
    mavenCentral()
    google()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClass.set("ru.droply.DroplyApplicationKt")
}

// Include migrations
sourceSets {
    main {
        resources {
            srcDirs("../droply-data/migrations")
            exclude("generated.yml")
        }
    }
}

dependencies {
    // Droply deps
    implementation(project(":droply-sprintor"))
    implementation(project(":droply-data"))
    implementation(project(":droply-service"))
    implementation(project(":droply-scenes"))

    // Ktor
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // Google Auth
    implementation("com.google.api-client:google-api-client:1.33.1")

    // JWT
    implementation("com.auth0:java-jwt:3.18.3")
    implementation("org.bouncycastle:bcprov-jdk15on:1.69")

    // Logging
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // DB stuff (with Spring Data & PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Kotlin deps
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")

    // Test stuff
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    // Embedded Postgres for tests
    testImplementation("com.opentable.components:otj-pg-embedded:0.13.4")
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

/**
 * Task for running Droply backend locally.
 * Automatically enables 'test' profile.
 *
 * Thus, embedded postgres will be in use
 * and other test beans will be there.
 */
tasks.register<DefaultTask>("localrun") {
    setDependsOn(listOf("testClasses"))
    setFinalizedBy(listOf("bootRun"))

    doFirst {
        System.setProperty("droply.localRun", "true")
        System.setProperty("spring.profiles.active", "test")

        logger.warn("You are running a dev environment, only test profile is in use")
    }
}
tasks {
    bootRun {
        doFirst {
            if (System.getProperty("droply.localRun") == "true") {
                systemProperties["spring.profiles.active"] = System.getProperty("spring.profiles.active")
                systemProperties["droply.localRun"] = "true"

                classpath = sourceSets.test.get().runtimeClasspath
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

/**
 * JIB (Docker build without daemon) settings
 */
jib {
    to {
        image = "registry.mine.theseems.ru/droply-backend"
        auth {
            username = System.getenv("DOCKER_REGISTRY_USERNAME")
            password = System.getenv("DOCKER_REGISTRY_PASSWORD")
        }

        setAllowInsecureRegistries(true)
    }

    // Add keys to the image
    extraDirectories {
        paths {
            path {
                setFrom(file("keys"))
                into = "/app/keys"
            }
        }
    }
}
