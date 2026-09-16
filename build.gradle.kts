plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.3.0"
    id("io.freefair.lombok") version "9.4.0"
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
    maven("https://repo.extendedclip.com/releases/") {
        name = "extendedclip"
    }
    maven("https://repo.faststats.dev/releases") {
        name = "faststatsReleases"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly(files("libs/fake-api-1.21.11.local-SNAPSHOT.jar")) // This is a Paper build that contains PWT and Folia API backported, Folia doesn't officially implement this API yet so a modular project is not an option
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("org.jspecify:jspecify:1.0.0")

    implementation("de.bsommerfeld.jshepherd:core:4.1.1")
    implementation("de.bsommerfeld.jshepherd:yaml:4.1.1")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("dev.faststats.metrics:bukkit:0.30.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
        jvmArgs("-Xms2G", "-Xmx2G")

        downloadPlugins {
            modrinth("luckperms", "v5.5.71-bukkit")
            modrinth("placeholderapi", "2.12.3")
            modrinth("viaversion", "5.11.0")
        }
    }

    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        minimize() {
            exclude(dependency("de.bsommerfeld.jshepherd:yaml"))
        }
        archiveClassifier.set("")

        mapOf(
            "org.bstats" to "bstats",
            "de.bsommerfeld.jshepherd" to "jshepherd",
            "dev.faststats" to "faststats",
        ).forEach { (key, value) ->
            relocate(key, "net.serlith.purpur.libs.$value")
        }

        mergeServiceFiles()
    }

    jar {
        archiveClassifier.set("dev")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
