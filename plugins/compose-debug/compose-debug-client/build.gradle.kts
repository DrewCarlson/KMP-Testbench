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
            }
        }
    }
}
