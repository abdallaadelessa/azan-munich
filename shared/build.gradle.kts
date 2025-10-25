import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.android.library)
}

version = "1.0"

android {
    namespace = "com.alifwyaa.azanmunich.shared"
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
    androidTarget()
//    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
//        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
//            ::iosArm64
//        else
//            ::iosX64
//
//    iosTarget("ios") {}
//
//    cocoapods {
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "14.1"
//        frameworkName = "shared"
//        podfile = project.file("../iosApp/Podfile")
//        pod(Deps.IOS.firebaseCore)
//        pod(Deps.IOS.firebaseFireStore)
//        pod(Deps.IOS.firebaseFireStoreSwift)
//        pod(Deps.IOS.firebaseCrashlytics)
//        pod(Deps.IOS.firebaseAnalytics)
//        pod(Deps.IOS.rxSwift)
//    }

    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // BOMs for Common dependencies
                implementation(project.dependencies.platform(libs.kotlin.bom))
                implementation(project.dependencies.platform(libs.koin.bom))
                // Dependencies
                implementation(libs.coroutines.core)
                implementation(libs.coroutine.worker)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test.common)
                implementation(libs.kotlin.test.annotations.common)
                implementation(libs.mockk.common)
            }
        }

        val androidMain by getting {
            dependencies {
                // BOMs for Android-specific dependencies
                implementation(project.dependencies.platform(libs.kotlin.bom))
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(project.dependencies.platform(libs.firebase.bom))
                // Dependencies
                implementation(libs.androidx.core)
                implementation(libs.androidx.core.ktx)
                implementation(libs.coroutines.android)
                implementation(libs.androidx.work.runtime.ktx)
                implementation(libs.coroutines.play.services)
                implementation(libs.bundles.firebase)
                implementation(libs.grpc.okhttp)
                implementation(libs.instabug)
                implementation(libs.bundles.play.review)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlin.test.junit)
                implementation(libs.junit)
                implementation(libs.mockk)
            }
        }

//        val iosMain by getting {
//            dependencies {
//                // empty
//            }
//        }
//        val iosTest by getting
    }
}
