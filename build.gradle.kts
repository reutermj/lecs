import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.mark"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url="https://dl.bintray.com/arrow-kt/arrow-kt/")
}

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.21"
}

val arrowVersion = "0.11.0"

dependencies {
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    testImplementation(kotlin("test-junit"))
    kapt("io.arrow-kt:arrow-meta:$arrowVersion")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}