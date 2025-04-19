pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.9.1" apply false
        id("org.jetbrains.kotlin.android") version "2.1.20" apply false
        id("com.google.dagger.hilt.android") version "2.56.1" apply false
        id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PetHosting"
include(":app")
