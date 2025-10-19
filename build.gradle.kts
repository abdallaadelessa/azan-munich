val isDevMode = java.util.Properties().apply {
    val localPropsFile = file("$rootDir/local.properties")
    if (localPropsFile.exists()) {
        load(localPropsFile.reader())
    }
}.getOrDefault("isDevMode", true).toString().toBoolean()

println("isDevMode: $isDevMode")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.detekt)
}

allprojects {
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

