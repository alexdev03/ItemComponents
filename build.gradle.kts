import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "8.3.2"
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "org.alexdev"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.paperApi)
    compileOnly(libs.bstatsBukkit)
    compileOnly(libs.configlib)
    compileOnly(libs.configlibPaper)
    compileOnly(libs.bstatsBukkit)

    implementation(libs.universalScheduler)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)

}


tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

version = rootProject.version

tasks.named<Jar>("jar").configure {
    dependsOn("shadowJar")
    archiveBaseName.set("${rootProject.name}-${project.name.replaceFirstChar { it.uppercase() }}")
}
tasks.named<Delete>("clean").configure {
}
tasks.named<ShadowJar>("shadowJar") {
    val relocation = "org.alexdev.itemcomponents.libraries."
    relocate("com.github.Anon8281.universalScheduler", relocation + "universalScheduler")

    dependencies {
        exclude(dependency(":kotlin-stdlib"))

    }

    destinationDirectory.set(file("$rootDir/target"))
    archiveFileName.set("${project.name}.jar")

    minimize()
}

tasks {
    runServer {
        minecraftVersion("1.21.3")

        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.6")
            url("https://download.luckperms.net/1556/bukkit/loader/LuckPerms-Bukkit-5.4.141.jar")
//            github("ViaVersion", "ViaVersion", "5.1.1", "ViaVersion-5.1.1.jar")
//            github("ViaVersion", "ViaBackwards", "5.1.1", "ViaBackwards-5.1.1.jar")
//            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
        }
    }
    runPaper.folia.registerTask {
        minecraftVersion("1.21.1")

        downloadPlugins {
            github("Anon8281", "PlaceholderAPI", "2.11.7", "PlaceholderAPI-2.11.7-DEV-Folia.jar")

            url("https://cdn.modrinth.com/data/HQyibRsN/versions/J2guR3GH/MiniPlaceholders-Paper-2.2.4.jar")
            url("https://ci.lucko.me/job/LuckPerms-Folia/lastSuccessfulBuild/artifact/bukkit/loader/build/libs/LuckPerms-Bukkit-5.4.141.jar")
            url("https://github.com/retrooper/packetevents/releases/download/v2.4.0/packetevents-spigot-2.4.0.jar")
            github("ViaVersion", "ViaVersion", "5.0.1", "ViaVersion-5.0.1.jar")
        }
    }
}

tasks.processResources {
    from("src/main/resources") {
        include("plugin.yml")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        expand(
            "configlibVersion" to libs.versions.configlibVersion.get(),
            "version" to project.version
        )
    }

}
