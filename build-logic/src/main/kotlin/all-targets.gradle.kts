import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

configure<LibraryExtension> {
    namespace = "build.wallet.${project.name.replace("-", "")}"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JAVA_VERSION
        targetCompatibility = JAVA_VERSION
    }
}

configure<KotlinMultiplatformExtension> {
    allTargets()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}
