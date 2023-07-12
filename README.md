# Azan Munich

Welcome to Azan Munich, a mobile application that provides prayer times for the Islamic community in Munich. The app is available for download on both the Apple Store and the Play Store.

## Installation

- Apple Store: [Download from Apple Store](https://apps.apple.com/app/id1607651736)
- Play Store: [Download from Play Store](https://play.google.com/store/apps/details?id=com.alifwyaa.azanmunich.android)

## Shared Code

The codebase for Azan Munich is shared between the Android and iOS platforms using Kotlin as the primary programming language. The shared code is structured as follows:
- `shared/commonMain/` contains the Kotlin Core code.
- `shared/androidMain/` contains the Kotlin JVM code for the Android platform.
- `shared/iosMain/` contains the Kotlin Native code for the iOS platform.

## Android

The Android app and associated widgets can be found in the `androidApp/` directory.

#### Static Analysis Tools

Lint & Detekt checks are enabled locally and on the CI system to ensure code quality and identify issues early on.
It is crucial to address any check issues before pushing your code, as failure to do so will prevent successful code submission.

During development, if you wish to disable all checks, simply add the following line to your `local.properties` file.
```properties
isDevMode=true
```

To manually run the pre-checks, use the following command:
```
./gradlew prePushChecksTask
```

## iOS
The iOS app and associated widgets can be found in the following directories:
- `iosApp/iosApp/`
- `iosApp/iosWidgets/`
#### Static Analysis Tools
SwiftLint checks are enabled locally
To run SwiftLint manually, use the following command:
```
iosApp/Pods/SwiftLint/swiftlint
```
The SwiftLint configuration file can be found in `iosApp/.swiftlint.yml`.

## Firebase
The Firebase project includes cloud functions that fetch Azan times from the [Islamic Center of Munich](https://www.islamisches-zentrum-muenchen.de/) and insert them into Cloud Firestore.
The Firebase code can be found in the `firebase/functions` directory.

## Contributing

We welcome and encourage contributions to the development of Azan Munich! Whether you have ideas for new features, bug fixes, or improvements, we appreciate your input. To contribute, please follow these steps:

1. Fork the repository and create a new branch for your feature or bug fix.
2. Commit your changes and push them to your forked repository.
3. Submit a pull request, detailing the changes you've made and any relevant information.

Our team will review your contribution and provide feedback. We value the effort and dedication of our contributors and look forward to collaborating with you to make Azan Munich even better!

Thank you for considering contributing to the project!

