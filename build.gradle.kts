plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
    id("io.freefair.lombok") version "8.13.1"
}

group = "net.serlith"
version = "3.0-SNAPSHOT"

allprojects {
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }
        maven("https://repo.extendedclip.com/releases/") {
            name = "extendedclip"
        }
        maven("https://jitpack.io") {
            name = "jitpack"
        }
    }
}

