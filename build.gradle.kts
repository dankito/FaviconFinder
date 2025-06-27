import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}


repositories {
    mavenCentral()
}


group = "net.dankito.utils"
version = "1.5.3-SNAPSHOT"

ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/FaviconFinder"
ext["projectDescription"] = "Extracts the favicons from a web site"


kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // suppresses compiler warning: [EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING] 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")

        // avoid "variable has been optimised out" in debugging mode
        if (System.getProperty("idea.debugger.dispatch.addr") != null) {
            freeCompilerArgs.add("-Xdebug")
        }
    }


    jvmToolchain(11) // TODO: when new ktor2-web-client version is out, set back to JVM 8

    jvm()

    js(IR) {
        binaries.library()

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {
            testTask {
                useMocha {
                    timeout = "20s" // Mocha times out after 2 s, which is too short for bufferExceeded() test
                }
            }
        }
    }

//    wasmJs() // Ktor 2 does not support WASM


    linuxX64()
    mingwX64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()

    applyDefaultHierarchyTemplate()


    val kotlinxSerializationVersion: String by project
    val jsoupVersion: String by project
    val webClientVersion: String by project
    val klfVersion: String by project

    val coroutinesVersion: String by project
    val assertKVersion: String by project
    val logbackVersion: String by project

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")

            implementation("net.dankito.web:web-client-api:$webClientVersion")

            implementation("net.codinux.log:klf:$klfVersion")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

            implementation("net.dankito.web:ktor2-web-client:$webClientVersion")

            implementation("com.willowtreeapps.assertk:assertk:$assertKVersion")
        }

        jvmMain.dependencies {
            implementation("org.jsoup:jsoup:$jsoupVersion")
        }
        jvmTest.dependencies {
            implementation("ch.qos.logback:logback-classic:$logbackVersion")
        }
    }
}


if (File(projectDir, "./gradle/scripts/publish-dankito.gradle.kts").exists()) {
    apply(from = "./gradle/scripts/publish-dankito.gradle.kts")
}