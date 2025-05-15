import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}
val authBaseUrl = localProperties["AUTH_BASE_URL"] as String
val smartBaseUrl = localProperties["SMART_BASE_URL"] as String
val baseUrl = localProperties["BASE_URL"] as String
val deviceBaseUrl = localProperties["DEVICE_BASE_URL"] as String


android {
    namespace = "com.hogumiwarts.lumos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hogumiwarts.lumos"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "AUTH_BASE_URL", "\"$authBaseUrl\"")
        buildConfigField("String", "SMART_BASE_URL", "\"$smartBaseUrl\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "DEVICE_BASE_URL", "\"$deviceBaseUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 하위 모듈
    implementation(project(":domain"))
    implementation(project(":data"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM + UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.compose.material)  // Material 1.x (옵션)
    debugImplementation(libs.androidx.ui.tooling)

    // Navigation
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // 이미지 로딩
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // Paging
    implementation(libs.paging)
    implementation(libs.paging.compose)

    // Lottie
    implementation(libs.lottie)
    implementation(libs.lottie.compose)

    // Logging
    implementation(libs.timber)

    // DataStore
    implementation(libs.datastore.preferences)

    // Serialization
    implementation(libs.kotlin.serialization)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Material / AppCompat (필요 시)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // UWB
    implementation("androidx.core.uwb:uwb:1.0.0-alpha10")

    // Orbit MVI core 기능
    implementation("org.orbit-mvi:orbit-core:9.0.0")
    // Android ViewModel 통합
    implementation("org.orbit-mvi:orbit-viewmodel:9.0.0")
    // Jetpack Compose 통합
    implementation("org.orbit-mvi:orbit-compose:9.0.0")

    // 위치 정보
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // constraintlayout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // 기타
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.3-beta")
    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp3
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)
    implementation("androidx.compose.foundation:foundation:1.4.3")

    implementation("com.google.android.gms:play-services-wearable:18.1.0")


    // color-picker
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") 


}