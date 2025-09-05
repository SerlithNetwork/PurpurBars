plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
    id("io.freefair.lombok") version "8.13.1"
}

group = "net.serlith"
version = "3.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    // compileOnly(files("libs/fish-api-1.21.8-R0.1-SNAPSHOT.jar")) // Used just for intellisense on PWT API
    // compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT") // Used just for intellisense of Folia API

    implementation("net.serlith.ConfigAPI:ConfigAPI-core:1.2.5")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 17 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    minimize()
    archiveClassifier.set("")

    mapOf(
        "org.bstats" to "metrics",
        "net.j4c0b3y.api.config" to "config",
        "dev.dejvokep.boostedyaml" to "boostedyaml",
    ).forEach { key, value ->
        relocate(key, "net.serlith.purpur.libs.$value")
    }

}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("*plugin.yml") {
        expand(props)
    }
}
