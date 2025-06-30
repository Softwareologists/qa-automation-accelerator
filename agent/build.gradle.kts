plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":plugins:http-emulator"))
    implementation(project(":plugins:fileio-emulator"))
    implementation(project(":plugins:jar-launcher"))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.springdoc.openapi.webflux.ui)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.dataformat.yaml)

    testImplementation(kotlin("test"))
    testImplementation(libs.spring.boot.starter.test)
}

java.sourceCompatibility = JavaVersion.VERSION_21
