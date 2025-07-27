repositories {
    mavenCentral()
    google()
}

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

application {
    mainClass.set("com.kmp.explore.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    // Shared module
    implementation(project(":shared"))

    // Ktor Server (Chapters 1-3)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)

    // Ktor Client for NASA API (Chapters 1-3)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)

    // Serialization (Chapters 1-3)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Database (Chapters 2-3)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.sqlite.jdbc)
    implementation(libs.hikaricp)

    // Dependency Injection (Chapters 1-3)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Logging (Chapters 1-3)
    implementation(libs.logback)

    // Testing (Chapters 1-3)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test)
}