plugins {
    id("cobblemon.base-conventions")
    id("com.github.johnrengelman.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks {

    jar {
        archiveBaseName.set("VeryScuffedCobblemonBreeding-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("VeryScuffedCobblemonBreeding-${project.name}")
        configurations = listOf(bundle)
        mergeServiceFiles()
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("VeryScuffedCobblemonBreeding-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }

}