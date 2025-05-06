// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

// rootProject/build.gradle.kts (또는 settings.gradle.kts)
val localProperties = java.util.Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}
extra["AUTH_BASE_URL"] = localProperties["AUTH_BASE_URL"] as String
