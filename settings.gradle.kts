import java.util.Locale

pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            library("asm-commons", "org.ow2.asm", "asm-commons").version("9.5")

            library("simpleyaml", "com.github.Carleslc.Simple-YAML", "Simple-Yaml").version("1.8.4")
            library("snakeyaml", "org.yaml", "snakeyaml").version("2.2")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

if (!file(".git").exists()) {
    val errorText = """
        =====================[ ERROR ]=====================
         The Kelp project directory is not a properly cloned Git repository.
         
         In order to build Kelp from source you must clone
         the repository using Git, not download a code zip from GitHub.
         
         See https://github.com/ryderbelserion/Kelp/blob/HEAD/CONTRIBUTING.md
         for further information on building and modifying Kelp.
        ===================================================
    """.trimIndent()

    error(errorText)
}

rootProject.name = "Kelp"

for (name in listOf("Kelp-API", "Kelp-Server")) {
    val projectName = name.lowercase(Locale.ENGLISH)
    include(projectName)
    findProject(":$projectName")!!.projectDir = file(name)
}