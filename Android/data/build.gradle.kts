import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("kotlin-parcelize")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

val smartBaseUrl = localProperties["SMART_BASE_URL"] as String
val baseUrl = localProperties["BASE_URL"] as String
val deviceBaseUrl = localProperties["DEVICE_BASE_URL"] as String

android {
    namespace = "com.hogumiwarts.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "AUTH_BASE_URL", "\"${rootProject.extra["AUTH_BASE_URL"]}\"")
        buildConfigField("String", "SMART_BASE_URL", "\"$smartBaseUrl\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "DEVICE_BASE_URL", "\"$deviceBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Domain 모듈
    implementation(project(":domain"))

    // 네트워크
    implementation(platform(libs.okhttp.bom))
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Serialization
    implementation(libs.kotlin.serialization)

    // DataStore
    implementation(libs.datastore.preferences)

    // Paging
    implementation(libs.paging)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Logging
    implementation(libs.timber)

    // DataStore
    implementation(libs.datastore.preferences)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}