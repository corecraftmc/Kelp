import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    id("io.papermc.paperweight.patcher") version "1.5.11"

    `java-library`
    `maven-publish`
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    repositories {
        maven("https://jitpack.io")

        maven(paperMavenPublicUrl)

        mavenCentral()
    }
}

repositories {
    mavenCentral()

    maven(paperMavenPublicUrl) {
        content {
            onlyForConfigurations(configurations.paperclip.name)
        }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.8.6:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    paperclip("io.papermc:paperclip:3.0.3")
}

paperweight {
    serverProject.set(project(":kelp-server"))

    remapRepo.set(paperMavenPublicUrl)
    decompileRepo.set(paperMavenPublicUrl)

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
    }
}

tasks.generateDevelopmentBundle {
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

allprojects {
    publishing {
        repositories {
            maven("https://repo.crazycrew.us/snapshots") {
                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }
            }
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

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.register("printKelpVersion") {
    doLast {
        println(project.version)
    }
}