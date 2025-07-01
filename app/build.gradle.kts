plugins {
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":plugins:http-emulator"))
    implementation(project(":plugins:fileio-emulator"))
    implementation(project(":plugins:jar-launcher"))
    implementation(libs.clikt)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)

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
        buildTypes {
            release {
                proguard {
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }
            }
        }
    }
}

tasks.register<JavaExec>("runCli") {
    group = "application"
    mainClass.set("tech.softwareologists.qa.app.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
}
