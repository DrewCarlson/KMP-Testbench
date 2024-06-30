plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradleConventions)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}
