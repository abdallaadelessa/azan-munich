import org.jetbrains.kotlin.gradle.dsl.JvmTarget

apply(from = "properties.gradle.kts")
val keyStoreFileName: String by extra
val keystorePassword: String by extra
val keystoreKeyAlias: String by extra
val keystoreKeyPassword: String by extra
val appVersionCode: String by extra
val appVersionName: String by extra


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.alifwyaa.azanmunich.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.alifwyaa.azanmunich.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode.toIntOrNull()
        versionName = appVersionName
        // Required when setting minSdkVersion to 20 or lower
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
        buildConfig = true
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 17
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    signingConfigs {
        create("default") {
            storeFile = file(keyStoreFileName)
            storePassword = keystorePassword
            keyAlias = keystoreKeyAlias
            keyPassword = keystoreKeyPassword
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("default")

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("default")

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
        }
    }

    lint {
        //list of issueId's which will be handled as warning
        val lintWarning = arrayOf(
            "ExtraTranslation",
            "AppCompatCustomView",
            "NewerVersionAvailable",
        )

        //list of issueId's which will be handled as error
        val lintError = arrayOf(
            "ApplySharedPref",
            "CheckResult",
            "DalvikOverride",
            "DefaultLocale",
            "DuplicateIds",
            "DuplicatePlatformClasses",
            "EllipsizeMaxLines",
            "ExifInterface",
            "HardcodedText",
            "IconDensities",
            "IconDuplicatesConfig",
            "InconsistentLayout",
            "InefficientWeight",
            "LogConditional",
            "MissingConstraints",
            "MissingDefaultResource",
            "MissingPermission",
            "NoHardKeywords",
            "DalvikOverride",
            "DefaultLocale",
            "DuplicateIds",
            "DuplicatePlatformClasses",
            "EllipsizeMaxLines",
            "ExifInterface",
            "HardcodedText",
            "IconDensities",
            "IconDuplicatesConfig",
            "InconsistentLayout",
            "InefficientWeight",
            "LogConditional",
            "MissingConstraints",
            "MissingDefaultResource",
            "MissingPermission",
            "NoHardKeywords",
            "ObsoleteLayoutParam",
            "ObsoleteSdkInt",
            "OnClick",
            "PxUsage",
            "RedundantNamespace",
            "Registered",
            "RequiredSize",
            "RestrictedApi",
            "ScrollViewSize",
            "SimpleDateFormat",
            "SwitchIntDef",
            "TextFields",
            "TypographyEllipsis",
            "UnknownNullness",
            "UnusedAttribute",
            "UseSparseArrays",
            "UselessLeaf",
            "UselessParent",
            "ValidActionsXml",
            "WebViewLayout",
            "WrongConstant",
            // "SpUsage", not intended to use
        )

        //list of issueId's which will be disabled
        val lintDisable = arrayOf(
            "MissingLeanbackLauncher",
            "MissingLeanbackSupport",
            "ImpliedTouchscreenHardware",
            // This check is incredibly slow: https://groups.google.com/g/lintdev/c/RGTvK_uHQGQ/m/FjJA12aGBAAJ
            "WrongThreadInterprocedural",
            "UnusedResources",
        )

        abortOnError = true
        checkDependencies = true
        checkReleaseBuilds = true
        checkTestSources = false
        showAll = true
        warningsAsErrors = true
        xmlReport = false
        error += lintError.toSet()
        warning += lintWarning.toSet()
        disable += lintDisable.toSet()
        lintConfig = file("lint.xml")
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(project(":shared"))

    // BOMs for version management
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    // Firebase
    implementation(libs.bundles.firebase)

    implementation(libs.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)

    // Compose
    implementation(libs.bundles.compose)
    implementation(libs.compose.material.icons.core)

    // AndroidX Compose Integration
    implementation(libs.bundles.androidx.compose.integration)

    // Accompanist
    implementation(libs.bundles.accompanist)

    // UI Tests
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.fastlane.screengrab)
}
