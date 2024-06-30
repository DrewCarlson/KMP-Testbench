plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.gradleConventions)
    implementation(libs.android.gradlePlugin)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}
