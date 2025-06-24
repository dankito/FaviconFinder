pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version kotlinVersion
    }
}


rootProject.name = "favicon-finder"