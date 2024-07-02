plugins {
    id("publish-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.datetime)
            }
        }
    }
}
