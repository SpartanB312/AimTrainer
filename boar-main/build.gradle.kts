plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.luna5ama.dev/")
}

val library: Configuration by configurations.creating
val projectModule: Configuration by configurations.creating

val lwjglVersion = "3.3.3"
val kmogusVersion= "1.0-SNAPSHOT"

val platforms = arrayOf(
    "natives-windows-x86", // Windows 32bit
    "natives-windows", // Windows 64bit
    "natives-windows-arm64", // Windows 64bit for ARM
    "natives-linux", // Linux 64bit
    "natives-linux-arm32", // Linux 32bit for ARM
    "natives-linux-arm64", // Linux 64bit for ARM
    "natives-macos", // MacOS 64bit
    "natives-macos-arm64" // MacOS 64bit for ARM
)

dependencies {
    projectModule(project(":boar-launch"))

    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    library("org.jetbrains.kotlin:kotlin-reflect:1.9.21")

    library("it.unimi.dsi:fastutil:8.5.9")
    library("org.joml:joml:1.10.4")
    library("com.google.code.gson:gson:2.10")
    library("com.google.guava:guava:32.0.0-android")

    // Audio System
    library("com.googlecode.soundlibs:mp3spi:1.9.5.4")

    // Render Engine
    library(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    library("org.lwjgl", "lwjgl")
    library("org.lwjgl", "lwjgl-assimp")
    library("org.lwjgl", "lwjgl-glfw")
    library("org.lwjgl", "lwjgl-openal")
    library("org.lwjgl", "lwjgl-opencl")
    library("org.lwjgl", "lwjgl-opengl")
    library("org.lwjgl", "lwjgl-stb")
    library("dev.luna5ama:kmogus-core:$kmogusVersion")
    library("dev.luna5ama:kmogus-struct-api:$kmogusVersion")

    // Networking
    library("io.netty:netty-all:4.1.110.Final")

    platforms.forEach {
        library("org.lwjgl", "lwjgl", classifier = it)
        library("org.lwjgl", "lwjgl-assimp", classifier = it)
        library("org.lwjgl", "lwjgl-glfw", classifier = it)
        library("org.lwjgl", "lwjgl-openal", classifier = it)
        library("org.lwjgl", "lwjgl-opengl", classifier = it)
        library("org.lwjgl", "lwjgl-stb", classifier = it)
    }

    library("com.soywiz.korlibs.korma:korma-jvm:2.7.0") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation(library)
    api(projectModule)

    println(rootDir)
}

val collectModules = task("collectModules", type = Copy::class) {
    group = "build"
    projectModule.forEach {
        from(it)
    }
    into("$rootDir\\release")
}

val moveJar = task("moveJar", type = Copy::class) {
    group = "build"
    from("$buildDir\\libs\\boar-main.jar")
    into("$rootDir\\release\\engine")
}

val collectLibs = task("collectLibs", type = Copy::class) {
    group = "build"
    library.forEach {
        from(it)
    }
    into("$rootDir\\release\\libs")
}

val collectAssets = task("collectAssets", type = Copy::class) {
    group = "build"
    from("$projectDir\\src\\main\\resources\\assets\\")
    into("$rootDir\\release\\assets")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
        }
    }

    jar {
        exclude("assets/**")
        manifest {
            attributes(
                "Launch-Entry" to "net.spartanb312.everett.launch.Entry"
            )
        }
    }

    "build" {
        dependsOn(collectLibs, collectAssets)
        finalizedBy(collectModules, moveJar)
    }
}