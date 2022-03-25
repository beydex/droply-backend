val ktorVersion: String by project
val kotlinLoggingVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kotlinxSerializationVersion: String by project
val kotlinxCoroutinesCoreVersion: String by project

allprojects {
    group = "ru.droply"
    version = "0.0.1"

    afterEvaluate {
        repositories {
            mavenCentral()
            google()
        }

        dependencies {
            // Logging
            implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

            // Kotlin deps
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesCoreVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$kotlinxSerializationVersion")
        }
    }
}

plugins {
    application

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    id("org.sonarqube") version "3.3"
}

/**
 * SonarQube settings
 */
sonarqube {
    properties {
        property("sonar.projectKey", "beydex_droply-backend")
    }
}
