plugins {
    id("mayonez.application")

    id("com.github.johnrengelman.shadow")
    id("org.jetbrains.dokka")
}

description = "The testbed project for Mayonez Engine that contains all the demo scenes."

dependencies {
    implementation(project(":mayonez-base"))
}

// Plugins

application { // For running project
    mainModule.set(mainModuleName)
    mainClass.set(mainClassName)
}

tasks {
    shadowJar { // For building fat jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier.set("")
    }
}