pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")   // 🔥 ADD HERE ALSO
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)  // ✅ FIXED
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")   // ✅ KEEP THIS
    }
}

rootProject.name = "Cash Cactus"
include(":app")