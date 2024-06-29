plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(libs.kotlin.compilerEmbeddable)
    compileOnly(libs.kotlin.stdlib)
}
