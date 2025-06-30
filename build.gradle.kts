plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    if (name != "agent") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "io.gitlab.arturbosch.detekt")
    }

    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    if (name != "agent") {
        extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            ignoreFailures.set(true)
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "20"
        ignoreFailures = true
    }
}

tasks.register("lint") {}

gradle.projectsEvaluated {
    tasks.named("lint") {
        dependsOn(subprojects.filter { it.name != "agent" }.map { it.tasks.named("ktlintCheck") })
        dependsOn(subprojects.filter { it.name != "agent" }.map { it.tasks.named("detekt") })
    }
}
