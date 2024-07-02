plugins {
    id("all-targets")
    id("publish-library")
}

kotlin {
    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                implementation(projects.pluginToolkitClient)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.serialization)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
    }
}
