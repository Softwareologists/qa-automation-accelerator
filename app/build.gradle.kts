plugins {
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    testImplementation(kotlin("test"))
    testImplementation(libs.compose.ui.test.junit4)
}


compose.desktop {
    application {
        mainClass = "tech.softwareologists.qa.app.ComposeMainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
        }
    }
}

tasks.register<JavaExec>("runCli") {
    group = "application"
    mainClass.set("tech.softwareologists.qa.app.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
}
