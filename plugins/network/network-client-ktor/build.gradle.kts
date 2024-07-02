plugins {
    id("publish-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.client.core)
            }
        }
    }
}
