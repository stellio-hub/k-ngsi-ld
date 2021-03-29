plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"

    kotlin("kapt") version "1.3.61"
    `java-library`
    `maven-publish`
}

repositories {
    // Use JCenter for resolving dependencies.
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
}

val arrowVersion = "0.11.0"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    "kapt"("io.arrow-kt:arrow-meta:$arrowVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
    implementation("com.jayway.jsonpath:json-path:2.0.0")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

version = "0.1.0"
group = "io.egm.kngsild"

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to "kngsild",
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("kngsild") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/stellio-hub/kngsild")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

detekt {
    toolVersion = "1.16.0"
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("$rootDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    baseline = file("$projectDir/config/detekt/baseline.xml")

    reports {
        xml.enabled = true
        txt.enabled = false
        html.enabled = true
    }
}
