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
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly(files("libs/paper-api-1.19.4-R0.1-SNAPSHOT.jar")) // This is a Paper build that contains PWT and Folia API backported
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("net.serlith.ConfigAPI:ConfigAPI-core:1.2.5")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21")
        jvmArgs("-Xms2G", "-Xmx2G")

        downloadPlugins {
            modrinth("luckperms", "v5.5.17-bukkit")
        }
    }

    shadowJar {
        minimize()
        archiveClassifier.set("")

        mapOf(
            "org.bstats" to "metrics",
            "net.j4c0b3y.api.config" to "config",
            "dev.dejvokep.boostedyaml" to "boostedyaml",
        ).forEach { (key, value) ->
            relocate(key, "net.serlith.purpur.libs.$value")
        }
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
