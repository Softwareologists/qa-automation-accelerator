dependencies {
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.dataformat.yaml)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.json.schema.validator)
    testImplementation(kotlin("test"))
}

