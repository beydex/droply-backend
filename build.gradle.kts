import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
    kotlin("plugin.jpa") version "1.5.21"
}

group = "com.beydex"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.security:spring-security-oauth2-client")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.security:spring-security-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // https://mvnrepository.com/artifact/com.google.apis/google-api-services-oauth2
    implementation("com.google.apis:google-api-services-oauth2:v2-rev157-1.25.0")

    implementation("com.google.api-client:google-api-client")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
