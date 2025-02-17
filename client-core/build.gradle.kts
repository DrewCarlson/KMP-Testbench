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
                api(projects.pluginToolkitClient)
                api(libs.coroutines.core)
                api(libs.ktor.http)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.serialization)
            }
        }
    }
}
