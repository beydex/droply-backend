group = "ru.droply.scenes"
version = "0.0.1"

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val googleApiClientVersion: String by project
val javaJwtVersion: String by project
val bcprovVersion: String by project
val mapstructVersion: String by project

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    kotlin("kapt")

    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {
    // Droply deps
    implementation(project(":droply-data"))
    implementation(project(":droply-sprintor"))
    implementation(project(":droply-service"))

    // Ktor
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Google Auth
    implementation("com.google.api-client:google-api-client:$googleApiClientVersion")

    // JWT
    implementation("com.auth0:java-jwt:$javaJwtVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:$bcprovVersion")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

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
