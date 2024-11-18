plugins {
    java
    id("com.gradleup.shadow") version ("8.3.2")
    id("xyz.jpenilla.run-paper") version ("2.3.1")
}

group = "me.xginko.betterworldstats"
version = "1.11.0"
description = "Show stats about server age, map size and unique player joins on your minecraft server."

repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }

    maven("https://ci.pluginwiki.us/plugin/repository/everything/") {
        name = "configmaster-repo"
    }

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "placeholderapi"
    }

    maven("https://mvn-repo.arim.space/lesser-gpl3/") {
        name = "arim-mvn-lgpl3"
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("org.jetbrains:annotations:24.1.0")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-ansi:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-logger-slf4j:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")

    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("space.arim.morepaperlib:morepaperlib:0.4.3")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.3") // 2.X for Java 8 targets
    implementation("com.github.thatsmusic99:ConfigurationMaster-API:v2.0.0-rc.1")
}

runPaper.folia.registerTask();

java.toolchain {
    languageVersion = JavaLanguageVersion.of(8)
}

tasks {
    runServer {
        minecraftVersion("1.20.6")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    build.configure {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("BetterWorldStats-${version}.jar")
        relocate("net.kyori", "me.xginko.betterworldstats.libs.kyori")
        relocate("org.bstats", "me.xginko.betterworldstats.libs.bstats")
        relocate("space.arim.morepaperlib", "me.xginko.betterworldstats.libs.morepaperlib")
        relocate("com.github.benmanes.caffeine", "me.xginko.betterworldstats.libs.caffeine")
        relocate("io.github.thatsmusic99.configurationmaster", "me.xginko.betterworldstats.libs.configmaster")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to project.description!!.replace('"'.toString(), "\\\""),
                    "url" to "https://github.com/xGinko/BetterWorldStats"
                )
            )
        }
    }
}
