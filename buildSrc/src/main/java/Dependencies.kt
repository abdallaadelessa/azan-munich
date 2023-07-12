/**
 * @author Created by Abdullah Essa on 26.09.21.
 */
object Versions {
    const val kotlin = "1.6.10"
    const val workManager = "2.7.0-rc01"
    const val coroutines = "1.5.0"
    const val mockk = "1.12.0"
}


/**
 * To define dependencies
 */
object Deps {
    object Shared {
        val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}" }
        val coroutineWorker by lazy { "com.autodesk:coroutineworker:0.7.0" }
        val kotlinDateTime by lazy { "org.jetbrains.kotlinx:kotlinx-datetime:0.2.0" }
        val settings by lazy { "com.russhwolf:multiplatform-settings-no-arg:0.7.7" }
        val serialization by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1" }
        val koin by lazy { "io.insert-koin:koin-core:3.1.2"}

        object Test {
            val mockk by lazy { "io.mockk:mockk-common:${Versions.mockk}" }
        }
    }

    object Android {
        val androidxCore by lazy { "androidx.core:core:1.1.0" }
        val androidxCoreKtx by lazy { "androidx.core:core-ktx:1.6.0" }
        val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}" }
        val coroutinesPlayServices by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutines}" }
        val firebaseBom by lazy { "com.google.firebase:firebase-bom:28.4.1" }
        val firebaseFireStore by lazy { "com.google.firebase:firebase-firestore:23.0.0" }
        val firebaseAnalytics by lazy { "com.google.firebase:firebase-analytics-ktx" }
        val firebaseCrashlytics by lazy { "com.google.firebase:firebase-crashlytics-ktx" }
        val grpcOkhttp by lazy { "io.grpc:grpc-okhttp:1.32.2" }
        val workManager by lazy { "androidx.work:work-runtime-ktx:${Versions.workManager}" }
        val instaBug by lazy { "com.instabug.library:instabug:10.9.0" }
        val playCore by lazy { "com.google.android.play:core:1.10.3" }
        val playCoreKtx by lazy { "com.google.android.play:core-ktx:1.8.1" }

        object Test {
            val mockk by lazy { "io.mockk:mockk:${Versions.mockk}" }
            val junit by lazy { "junit:junit:4.13.2" }
        }
    }

    object IOS {
        val firebaseCore by lazy { "FirebaseCore" }
        val firebaseFireStore by lazy { "FirebaseFirestore" }
        val firebaseFireStoreSwift by lazy { "FirebaseFirestoreSwift" }
        val firebaseCrashlytics by lazy { "FirebaseCrashlytics" }
        val firebaseAnalytics by lazy { "FirebaseAnalytics" }
        val rxSwift by lazy { "RxSwift" }
    }

}
