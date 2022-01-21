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
    kotlin("kapt") version "1.3.72"
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
    google()
}

// For JPA compatibility with Kotlin
// https://habr.com/ru/company/haulmont/blog/572574/
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // DB stuff (with Spring Data & PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Google Auth
    implementation("com.google.api-client:google-api-client:1.33.0")

    // Kotlin deps
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.0.Beta2")
    kapt("org.mapstruct:mapstruct-processor:1.5.0.Beta2")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    // Embedded Postgres for tests
    testImplementation("com.opentable.components:otj-pg-embedded:0.13.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
                systemProperties["spring.profiles.active"] = System.getProperty("spring.profiles.active")
                systemProperties["droply.localRun"] = "true"

                classpath = sourceSets.test.get().runtimeClasspath
            }
        }
    }
}

/**
 * Task for running Droply backend locally.
 * Automatically enables 'test' profile.
 *
 * Thus, embedded postgres will be in use
 * and other test beans will be there.
 */
@Suppress("LeakingThis")
abstract class RunLocally : DefaultTask() {
    @get:Input
    abstract val profiles: ListProperty<String>

    init {
        profiles.convention(listOf("test"))
        setDependsOn(listOf("testClasses"))
        setFinalizedBy(listOf("bootRun"))
    }
}

tasks.register<RunLocally>("runLocally") {
    doFirst {
        val profilesEnabledInfo = profiles.get().joinToString(" ")
        logger.error(profilesEnabledInfo)
        System.setProperty("droply.localRun", "true")
        System.setProperty("spring.profiles.active", profilesEnabledInfo)

        logger.warn("You are running a dev environment, profiles: $profilesEnabledInfo")
    }
}