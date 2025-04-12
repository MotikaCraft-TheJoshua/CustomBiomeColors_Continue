plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

group = "me.arthed"
version = "1.3.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "worldedit-repo"
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven("https://repo.papermc.io/repository/maven-public/")
    gradlePluginPortal()
}

dependencies {
    implementation(project(":utils"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName.set("CustomBiomeColors-${version}-MC-1.21.4.jar")
        minimize()
    }
}
