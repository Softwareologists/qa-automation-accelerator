plugins {
    application
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("tech.softwareologists.qa.app.MainKt")
}
