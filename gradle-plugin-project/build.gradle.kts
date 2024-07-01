import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "1.9.22"
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.gradleBuildConfig)
    alias(libs.plugins.mavenPublish)
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
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
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
        create("testbenchPlugin") {
            id = "org.drewcarlson.testbench"
            implementationClass = "testbench.gradle.TestBenchGradlePlugin"
            description = "Configure and run the KMP Test Bench Desktop client"
            tags.set(listOf("kotlin", "debugging", "multiplatform"))
        }
    }
}
