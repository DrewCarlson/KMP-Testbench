import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.serialization)
    id("all-targets")
}

kotlin {
    targets
        .filterIsInstance<KotlinNativeTarget>()
        .filter {
            it.name.contains("ios", ignoreCase = true) ||
                it.name.contains("watchos", ignoreCase = true) ||
                it.name.contains("tvos", ignoreCase = true)
        }.forEach { target ->
            target.compilations.getByName("main") {
                cinterops {
                    create("simulatorcheck") {
                        defFile("src/iosMain/cinterop/simulatorcheck.def")
                    }
                }
            }
        }
    sourceSets {
        all {
            explicitApi()
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvmMain {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}
