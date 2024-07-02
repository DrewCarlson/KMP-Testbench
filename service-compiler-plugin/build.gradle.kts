plugins {
    kotlin("jvm")
    id("publish-library")
}

apiValidation {
    validationDisabled = true
}

dependencies {
    compileOnly(libs.kotlin.compilerEmbeddable)
    compileOnly(libs.kotlin.stdlib)
}
