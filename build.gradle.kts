plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.2.0"
}

group = "com.instancify.scriptify.kts"
version = "1.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.instancify.app/snapshots")
}

dependencies {
    compileOnlyApi("com.instancify.scriptify:core:1.4.4-SNAPSHOT")
    compileOnlyApi("org.jetbrains.kotlin:kotlin-scripting-common:2.2.10")
    compileOnlyApi("org.jetbrains.kotlin:kotlin-scripting-jvm:2.2.10")
    compileOnlyApi("org.jetbrains.kotlin:kotlin-scripting-jvm-host:2.2.10")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "script"
            version = project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "instancify"
            url = uri("https://repo.instancify.app/snapshots")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}