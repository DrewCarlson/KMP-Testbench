plugins {
    kotlin("jvm")
    id("publish-library")
}

kotlin {
    sourceSets.all {
        explicitApi()
    }
}
