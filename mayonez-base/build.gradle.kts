plugins {
    id("mayonez.library-conventions")

    id(kotlinPlugin)
    id(dokkaPlugin)
}

description = "The library project for Mayonez Engine that contains the core and API classes."

dependencies {
    // Code Dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.json:json:20240303")

    // LWJGL Modules
    implementation("org.joml:joml:1.10.5")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion")) // Bill of materials: set version for all libs

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    implementation("org.lwjgl:lwjgl::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-stb::$lwjglNatives")
}

// Plugins and Tasks

tasks {
    compileJava {
        dependsOn(compileKotlin)
    }

    compileKotlin {
        compilerOptions {
            suppressWarnings.set(true)
            destinationDirectory = file("build/classes/java/main")
        }
    }
}
