group = "ru.droply.mapper"
version = "0.0.1"

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val liquibaseVersion: String by project
val mapstructVersion: String by project

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
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

    // DB stuff (with Spring Data & PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Migrations
    implementation("org.liquibase.ext:liquibase-hibernate5:$liquibaseVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")

    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
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

// Generate changelog only after our entities are compiled
tasks.findByPath("diffChangeLog")?.apply {
    dependsOn("jar")
}
