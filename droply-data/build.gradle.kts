group = "ru.droply.data"
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

    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

repositories {
    mavenCentral()
}

/**
 * Liquibase settings
 */
val liquibase = mutableMapOf(
    "referenceUrl" to
        "hibernate:spring:ru.droply.data.entity" +
        "?dialect=org.hibernate.dialect.PostgreSQL10Dialect" +
        "&hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy" +
        "&hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy",
    "mainChangeLog" to "migrations/changelog.yml",
    "newChangeLog" to "migrations/generated.yml",
    "referenceDriver" to "liquibase.ext.hibernate.database.connection.HibernateDriver",
    "driver" to "org.postgresql.Driver",
    "url" to (System.getenv("spring.datasource.url") ?: "jdbc:postgresql://localhost/droply"),
    "username" to (System.getenv("spring.datasource.username") ?: "postgres"),
    "password" to (System.getenv("spring.datasource.password") ?: "postgres")
)

// For JPA compatibility with Kotlin
// https://habr.com/ru/company/haulmont/blog/572574/
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

dependencies {
    // DB stuff (with Spring Data & PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Migrations
    implementation("org.liquibase.ext:liquibase-hibernate5:$liquibaseVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
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

tasks.register<JavaExec>("makemigrations") {
    dependencies {
        // Migrations
        implementation("org.liquibase.ext:liquibase-hibernate5:$liquibaseVersion")
        implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    }

    classpath(sourceSets.main.get().runtimeClasspath)
    classpath(configurations.findByName("liquibase"))

    group = "liquibase"
    mainClass.set("liquibase.integration.commandline.Main")

    args = listOf(
        "--logLevel=info",
        "--changeLogFile=${liquibase["newChangeLog"]}",
        "--referenceUrl=${liquibase["referenceUrl"]}",
        "--url=${liquibase["url"]}",
        "--driver=${liquibase["driver"]}",
        "--username=${liquibase["username"]}",
        "--password=${liquibase["password"]}",
        "diffChangeLog"
    )
}

tasks.register<JavaExec>("applymigrations") {
    dependencies {
        // Migrations
        implementation("org.liquibase.ext:liquibase-hibernate5:$liquibaseVersion")
        implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    }

    classpath(sourceSets.main.get().runtimeClasspath)
    classpath(configurations.findByName("liquibase"))

    group = "liquibase"
    mainClass.set("liquibase.integration.commandline.Main")

    args = listOf(
        "--logLevel=info",
        "--changeLogFile=${liquibase["mainChangeLog"]}",
        "--referenceUrl=${liquibase["referenceUrl"]}",
        "--url=${liquibase["url"]}",
        "--driver=${liquibase["driver"]}",
        "--username=${liquibase["username"]}",
        "--password=${liquibase["password"]}",
        "update"
    )
}

tasks.register<JavaExec>("diffmigrations") {
    dependencies {
        // Migrations
        implementation("org.liquibase.ext:liquibase-hibernate5:$liquibaseVersion")
        implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    }

    classpath(sourceSets.main.get().runtimeClasspath)
    classpath(configurations.findByName("liquibase"))

    group = "liquibase"
    mainClass.set("liquibase.integration.commandline.Main")

    args = listOf(
        "--logLevel=info",
        "--changeLogFile=${liquibase["mainChangeLog"]}",
        "--referenceUrl=${liquibase["referenceUrl"]}",
        "--url=${liquibase["url"]}",
        "--driver=${liquibase["driver"]}",
        "--username=${liquibase["username"]}",
        "--password=${liquibase["password"]}",
        "diff"
    )
}
