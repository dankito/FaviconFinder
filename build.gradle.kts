
buildscript {
    repositories {
        mavenCentral()
    }

    val kotlinVersion: String by extra

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}


group = "net.dankito.utils"
version = "1.0.5-SNAPSHOT"

ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/FaviconFinder"
ext["projectDescription"] = "Extracts the favicons from a web site"


plugins {
    kotlin("jvm")

    // So after executing publish staged repository can be closed and released by executing closeAndReleaseRepository
    id("io.codearte.nexus-staging") version "0.21.2"
}


kotlin {
    jvmToolchain(8)
}


repositories {
    mavenCentral()
}


val commonScriptsFile = File(File(project.gradle.gradleUserHomeDir, "scripts"), "publish-dankito.gradle.kts")
if (commonScriptsFile.exists()) {
    apply(from = commonScriptsFile)
}


val jsoupVersion: String by project
val jacksonVersion: String by project
val slf4jVersion: String by project

val assertKVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("org.slf4j:slf4j-api:$slf4jVersion")


    testImplementation(kotlin("test"))

    testImplementation("com.willowtreeapps.assertk:assertk:$assertKVersion")

    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
}
