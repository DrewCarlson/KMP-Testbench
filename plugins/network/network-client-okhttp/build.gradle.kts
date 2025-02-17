import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("com.android.library")
    id("publish-library")
}

kotlin {
    jvmToolchain(JAVA_VERSION.majorVersion.toInt())
    jvm()
    androidTarget()
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("jvmCommon") {
                withAndroidTarget()
                withJvm()
            }
        }
    }
    sourceSets {
        all {
            explicitApi()
        }
        val jvmCommonMain by getting {
            dependencies {
                implementation(libs.okhttp)
            }
        }
    }
}
