
buildscript {
    repositories {
        mavenCentral()
    }

    val kotlinVersion: String by extra

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}


plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


repositories {
    mavenCentral()
}


group = "net.dankito.utils"
version = "1.5.2-SNAPSHOT"

ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/FaviconFinder"
ext["projectDescription"] = "Extracts the favicons from a web site"


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


if (File(projectDir, "./gradle/scripts/publish-dankito.gradle.kts").exists()) {
    apply(from = "./gradle/scripts/publish-dankito.gradle.kts")
}