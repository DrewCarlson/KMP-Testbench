plugins {
    id("publish-library")
}

kotlin {
    jvmToolchain(JAVA_VERSION.majorVersion.toInt())
    jvm()
    sourceSets {
        all {
            explicitApi()
        }
        jvmMain {
            dependencies {
                implementation(libs.okhttp)
            }
        }
    }
}
