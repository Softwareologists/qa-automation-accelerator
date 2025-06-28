dependencies {
    implementation(project(":core"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.cio)
}
