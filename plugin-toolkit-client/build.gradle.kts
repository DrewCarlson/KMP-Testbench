plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                api(projects.pluginToolkitCore)
            }
        }
    }
}
