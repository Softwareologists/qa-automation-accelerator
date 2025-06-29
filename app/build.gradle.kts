plugins {
    application
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("tech.softwareologists.qa.app.ComposeMainKt")
}
