plugins {
    alias(libs.plugins.jvm)
    `java-gradle-plugin`
}

kotlin {
    explicitApi()
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.stdlib)
}
