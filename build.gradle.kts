import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.mark"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")

}

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.21"
    id("org.jetbrains.dokka") version "1.4.20"
    jacoco
}

val arrowVersion = "0.11.0"

dependencies {
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    testImplementation(kotlin("test-junit"))
    kapt("io.arrow-kt:arrow-meta:$arrowVersion")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.20")
    dokkaJekyllPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.20")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

tasks.dokkaJekyll.configure {
    outputDirectory.set(buildDir.resolve("dokka-jekyll"))
}

tasks.test {
    useJUnit()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        html.destination = file("${buildDir}/jacocoHtml")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}