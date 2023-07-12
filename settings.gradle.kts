pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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
