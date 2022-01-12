import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val testContainersVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

group = "ru.droply"
version = "0.0.1"
application {
    mainClass.set("ru.droply.DroplyApplicationKt")
}

repositories {
    mavenCentral()
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

dependencies {
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.opentable.components:otj-pg-embedded:0.13.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks {
    bootRun {
        doFirst {
            if (System.getProperty("droply.localRun") == "true") {
                logger.warn("Running locally, including test configurations")
                classpath = sourceSets.test.get().runtimeClasspath
            }
        }
    }
}

abstract class RunLocally : DefaultTask() {
    @get:Input
    abstract val profiles: ListProperty<String>

    init {
        profiles.convention(listOf("test"))
        setFinalizedBy(setOf("bootRun"))
    }
}

tasks.register<RunLocally>("runLocally") {
    profiles.set(listOf("test"))
    doFirst {
        System.setProperty("spring.profiles.active", profiles.get().joinToString(" "))
        System.setProperty("droply.localRun", "true")
        logger.warn("You are running a dev environment, profiles: ${profiles.get().joinToString(" ")}")
    }
}