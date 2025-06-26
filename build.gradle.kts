
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
    kotlin("plugin.serialization")
}


kotlin {
    jvmToolchain(11) // TODO: when new ktor2-web-client version is out, set back to JVM 8

    compilerOptions {
        // avoid "variable has been optimised out" in debugging mode
        if (System.getProperty("idea.debugger.dispatch.addr") != null) {
            freeCompilerArgs.add("-Xdebug")
        }
    }
}


repositories {
    mavenCentral()
}


group = "net.dankito.utils"
version = "1.5.3-SNAPSHOT"

ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/FaviconFinder"
ext["projectDescription"] = "Extracts the favicons from a web site"


val kotlinxSerializationVersion: String by project
val jsoupVersion: String by project
val webClientVersion: String by project
val klfVersion: String by project

val coroutinesVersion: String by project
val assertKVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")

    implementation("org.jsoup:jsoup:$jsoupVersion")

    implementation("net.dankito.web:web-client-api:$webClientVersion")

    implementation("net.codinux.log:klf:$klfVersion")


    testImplementation(kotlin("test"))
    
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    testImplementation("net.dankito.web:ktor2-web-client:$webClientVersion")

    testImplementation("com.willowtreeapps.assertk:assertk:$assertKVersion")

    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
}


if (File(projectDir, "./gradle/scripts/publish-dankito.gradle.kts").exists()) {
    apply(from = "./gradle/scripts/publish-dankito.gradle.kts")
}