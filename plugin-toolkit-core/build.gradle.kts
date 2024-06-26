plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }
    }
}
