plugins {
    id("publish-library")
}

kotlin {
    sourceSets.all {
        explicitApi()
    }
}
