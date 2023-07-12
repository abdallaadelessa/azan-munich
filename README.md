# Azan Munich #
This README would normally document whatever steps are necessary to get your application up and running.

## Shared Unit Tests
Run the following command
    ```
    ./gradlew :shared:testDebugUnitTest
    ```

## Static analysis tools ##

#### 1) Android (Lint & Detekt) & Shared (Detekt) ####
Lint & Detekt checks are enabled locally and also on the ci to detect problems as early as possible
- Please make sure to fix all check issues before pushing your code otherwise you won't be able to do it

- To disable all the checks while developing please add the following line to your `local.properties`
    ```
    isDevMode=true
    ```
- To run the pre checks manually use the following command
    ```
    ./gradlew prePushChecksTask
    ```

#### 2) IOS ####
- To run swift lint manually use the following command
    ```
    iosApp/Pods/SwiftLint/swiftlint
    ```
- The swiftlint config file can be found in `iosApp/.swiftlint.yml`
