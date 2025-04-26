buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.google.services.plugin)
    }
}

plugins {
    alias(libs.plugins.compose.compiler) apply false
    kotlin("jvm") version "1.9.0"
    application
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.3")
    implementation("io.ktor:ktor-server-netty:2.3.3")
    implementation("io.ktor:ktor-serialization-gson:2.3.3")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.twilio.sdk:twilio:9.8.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

