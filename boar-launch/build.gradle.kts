plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
}

tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "net.spartanb312.boar.launch.Main"
            )
        }
    }
}