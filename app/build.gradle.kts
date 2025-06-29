plugins {
    application
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)
    implementation(compose.desktop.currentOs)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("tech.softwareologists.qa.app.MainKt")
}
