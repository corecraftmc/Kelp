plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    id("io.papermc.paperweight.patcher") version "1.5.11"

    id("java-plugin")
}

allprojects {
    apply(plugin = "java-library")
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.8.10:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    paperclip("io.papermc:paperclip:3.0.3")
}

paperweight {
    serverProject.set(project(":kelp-server"))

    remapRepo = paperMavenPublicUrl
    decompileRepo = paperMavenPublicUrl

    useStandardUpstream("purpur") {
        url.set(github("PurpurMC", "Purpur"))
        ref.set(providers.gradleProperty("purpurCommit"))

        withStandardPatcher {
            baseName("Purpur")

            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("Kelp-API"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("Kelp-Server"))
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = layout.projectDirectory.dir("patches/generated-api")
            outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
        }
    }
}

publishing {
    publications.create<MavenPublication>("devBundle") {
        artifact(tasks.generateDevelopmentBundle) {
            artifactId = "dev-bundle"
        }
    }
}

tasks {
    generateDevelopmentBundle {
        apiCoordinates.set("com.rydrbelserion.kelp:kelp-api")
        mojangApiCoordinates.set("io.papermc.paper:paper-mojangapi")
        libraryRepositories.set(
            listOf(
                "https://repo.maven.apache.org/maven2/",
                paperMavenPublicUrl,
                "https://repo.crazycrew.us/snapshots",
            )
        )
    }

    register("printMinecraftVersion") {
        doLast {
            println(providers.gradleProperty("mcVersion").get().trim())
        }
    }

    register("printKelpVersion") {
        doLast {
            println(project.version)
        }
    }
}