import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "1.9.22"
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.gradleBuildConfig)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryCompat)
}

System
    .getenv("GITHUB_REF_NAME")
    ?.takeIf { it.startsWith("v") }
    ?.let { version = it.removePrefix("v") }

kotlin {
    explicitApi()
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_1_8)
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        jvmTarget.set(libs.versions.jvmTarget.map(JvmTarget::fromTarget))
        freeCompilerArgs.addAll("-Xjvm-default=all")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(libs.versions.jvmTarget.map(String::toInt))
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.stdlib)
}

buildConfig {
    packageName("testbench.gradle")
    buildConfigField("VERSION", version.toString())
    buildConfigField("COMPOSE_VERSION", libs.versions.compose)
    buildConfigField("SERIALIZATION_VERSION", libs.versions.serialization)

    forClass("TestbenchDeps") {
        buildConfigField("clientCore", "org.drewcarlson.testbench:client-core:$version")
        buildConfigField("clientNetworkKtor", "org.drewcarlson.testbench:network-client-ktor:$version")
        buildConfigField("clientNetworkOkhttp", "org.drewcarlson.testbench:network-client-okhttp:$version")
    }
}

gradlePlugin {
    plugins {
        create("testbenchPlugin") {
            id = "org.drewcarlson.testbench"
            implementationClass = "testbench.gradle.TestbenchGradlePlugin"
            description = "Configure and run the KMP Testbench Desktop client"
            tags.set(listOf("kotlin", "debugging", "multiplatform"))
        }
    }
}
