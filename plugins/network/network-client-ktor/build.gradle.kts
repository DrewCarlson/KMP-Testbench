plugins {
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.client.core)

                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }
    }
}
