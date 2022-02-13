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