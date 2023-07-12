val isDevMode = java.util.Properties().apply {
    val localPropsFile = file("$rootDir/local.properties")
    if (localPropsFile.exists()) {
        load(localPropsFile.reader())
    }
}.getOrDefault("isDevMode", true).toString().toBoolean()

println("isDevMode: $isDevMode")

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath(kotlin("serialization", version = Versions.kotlin))
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    setupDetekt()

    beforeEvaluate {
        activatePrePushChecksInCompileTime()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

//==================================================>
//region Detekt

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.17.1")
}

fun Project.setupDetekt() {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        parallel = false
        config = files("$rootDir/detekt/detekt-config.yml")
        input = files(
            "src/main/kotlin",
            "src/main/java",
            "src/androidMain/kotlin",
            "src/commonMain/kotlin",
            "src/commonTest/kotlin",
            "src/iosMain/kotlin",
        )
    }
}

//endregion
//==================================================>
//region pre-push git hook

File("$rootDir/.git/hooks/pre-push").delete()
if (!File("$rootDir/.git/hooks/pre-push").exists()) {
    copy {
        from("$rootDir/scripts/pre-push.sh") {
            rename { it.removeSuffix(".sh") }
        }
        into("$rootDir/.git/hooks")
        fileMode = 0b000_101_101_000
    }
}

val prePushChecksTask = task<Exec>("prePushChecksTask") {
    commandLine(".git/hooks/pre-push")
}

fun Project.activatePrePushChecksInCompileTime() {
    if (isDevMode) return

    tasks.configureEach {
        //println("configureEach: $project:$name")
        if (name == "assembleDebug" || name == "assembleRelease") {
            println("prePushChecksTask attached to $project:$name")
            dependsOn(prePushChecksTask)
        }
    }
}

//endregion

