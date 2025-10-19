pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://sdks.instabug.com/nexus/repository/instabug-cp/") }
    }
}

rootProject.name = "Azan-Munich"
include(":androidApp")
include(":shared")


buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
    remote(HttpBuildCache::class.java) {
        isEnabled = false
    }
}
