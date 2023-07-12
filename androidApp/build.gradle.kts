apply(from = "properties.gradle.kts")
val keyStoreFileName: String by extra
val keystorePassword: String by extra
val keystoreKeyAlias: String by extra
val keystoreKeyPassword: String by extra
val appVersionCode: String by extra
val appVersionName: String by extra


plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdkVersion(AndroidProjectConfig.compileSdkVersion)

    defaultConfig {
        applicationId = "com.alifwyaa.azanmunich.android"
        minSdkVersion(AndroidProjectConfig.minSdkVersion)
        targetSdkVersion(AndroidProjectConfig.targetSdkVersion)
        versionCode = appVersionCode.toIntOrNull()
        versionName = appVersionName
        // Required when setting minSdkVersion to 20 or lower
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerVersion = Versions.kotlin
        kotlinCompilerExtensionVersion = "1.2.0-alpha02"
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

    packagingOptions {
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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation(project(":shared"))

    implementation(Deps.Android.coroutines)

    implementation(Deps.Android.workManager)

    //====================>

    implementation("androidx.appcompat:appcompat:1.4.1")

    //====================>

    val composeVersion = "1.1.0"
    implementation("androidx.compose.ui:ui:$composeVersion")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    // Material Design
    implementation("androidx.compose.material:material:$composeVersion")

    //====================>

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    implementation("androidx.navigation:navigation-compose:2.4.1")
    implementation("androidx.activity:activity-compose:1.4.0")

    //====================>

    val accompanistVersion = "0.23.0"
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets-ui:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")

    //====================>

    // UI Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    // Fastlane
    androidTestImplementation("tools.fastlane:screengrab:2.1.1")

}
