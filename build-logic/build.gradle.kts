plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradleConventions)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.mavenPublish.gradlePlugin)
    implementation(libs.dokka.gradlePlugin)
    implementation(libs.bcv.gradlePlugin)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}
