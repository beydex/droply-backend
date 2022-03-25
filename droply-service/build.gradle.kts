group = "ru.droply.service"
version = "0.0.1"

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val googleApiClientVersion: String by project
val javaJwtVersion: String by project
val bcprovVersion: String by project

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
    implementation(project(":droply-sprintor"))
    implementation(project(":droply-data"))

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Google Auth
    implementation("com.google.api-client:google-api-client:$googleApiClientVersion")

    // JWT
    implementation("com.auth0:java-jwt:$javaJwtVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:$bcprovVersion")

    // DB stuff (with Spring Data & PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
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
