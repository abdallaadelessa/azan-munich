import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("native.cocoapods")
    id("com.android.library")
}

version = "1.0"

android {
    compileSdkVersion(AndroidProjectConfig.compileSdkVersion)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(AndroidProjectConfig.minSdkVersion)
        targetSdkVersion(AndroidProjectConfig.targetSdkVersion)
    }
}

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {}

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        frameworkName = "shared"
        podfile = project.file("../iosApp/Podfile")
        pod(Deps.IOS.firebaseCore)
        pod(Deps.IOS.firebaseFireStore)
        pod(Deps.IOS.firebaseFireStoreSwift)
        pod(Deps.IOS.firebaseCrashlytics)
        pod(Deps.IOS.firebaseAnalytics)
        pod(Deps.IOS.rxSwift)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Shared.coroutines)
                implementation(Deps.Shared.coroutineWorker)
                implementation(Deps.Shared.kotlinDateTime)
                implementation(Deps.Shared.serialization)
                implementation(Deps.Shared.settings)
                implementation(Deps.Shared.koin)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Deps.Shared.Test.mockk)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.Android.androidxCore)
                implementation(Deps.Android.androidxCoreKtx)
                implementation(Deps.Android.coroutines)
                implementation(Deps.Android.coroutinesPlayServices)
                implementation(project.dependencies.platform(Deps.Android.firebaseBom))
                implementation(Deps.Android.firebaseFireStore)
                implementation(Deps.Android.firebaseAnalytics)
                implementation(Deps.Android.firebaseCrashlytics)
                implementation(Deps.Android.grpcOkhttp)
                implementation(Deps.Android.instaBug)
                implementation(Deps.Android.playCore)
                implementation(Deps.Android.playCoreKtx)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-junit"))
                implementation(Deps.Android.Test.junit)
                implementation(Deps.Android.Test.mockk)
            }
        }

        val iosMain by getting {
            dependencies {
                // empty
            }
        }
        val iosTest by getting
    }
}
