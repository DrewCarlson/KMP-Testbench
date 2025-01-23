import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "2.1.20-firework.34"
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.gradleBuildConfig)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryCompat)
}

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
    if (gradle.parent == null) {
        compileOnly(libs.android.gradlePlugin)
        compileOnly(libs.kotlin.gradlePlugin)
    } else {
        implementation(libs.android.gradlePlugin)
        implementation(libs.kotlin.gradlePlugin)
    }
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.stdlib)
}

buildConfig {
    packageName("testbench.gradle")
    buildConfigField("VERSION", version.toString())
    buildConfigField("SERIALIZATION_VERSION", libs.versions.serialization)
}

gradlePlugin {
    plugins {
        create("testbenchSettingsPlugin") {
            id = "org.drewcarlson.testbench.plugin-toolkit"
            implementationClass = "testbench.gradle.TestBenchGradleSettingsPlugin"
            description = "Create custom plugins for kmp-testbench"
            tags.set(listOf("kotlin", "debugging", "multiplatform"))
        }
    }
}

System
    .getenv("GITHUB_REF_NAME")
    ?.takeIf { it.startsWith("v") }
    ?.let { version = it.removePrefix("v") }
