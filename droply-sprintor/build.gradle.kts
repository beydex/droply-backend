group = "ru.droply.sprintor"
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
}

repositories {
    mavenCentral()
}

dependencies {
    // Droply deps
    implementation(project(":droply-data"))

    // Ktor
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // Redisson
    implementation("org.redisson:redisson:3.13.1")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
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

tasks.findByName("bootJar")?.apply {
    enabled = false
}

tasks.findByName("jar")?.apply {
    enabled = true
}
