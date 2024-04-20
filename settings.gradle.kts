rootProject.name = "Boar"

// Modules
include(":boar-launch")
include(":boar-main")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.luna5ama.dev/")
    }
}