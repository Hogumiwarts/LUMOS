plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.hogumiwarts.lumos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hogumiwarts.lumos"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
    // Wearable Services
    implementation(libs.play.services.wearable)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // General Compose dependencies
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    // Material Icons (Extended)
    implementation("androidx.compose.material:material-icons-extended")

// Compose for Wear OS
    implementation("androidx.wear.compose:compose-material:1.4.0")
    implementation("androidx.wear.compose:compose-foundation:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:1.4.0") // Wear용 프리뷰 포함

// Horologist (Google의 Wear OS 보조 라이브러리)
    implementation("com.google.android.horologist:horologist-composables:0.6.20")
    implementation("com.google.android.horologist:horologist-compose-layout:0.6.20")
    implementation("com.google.android.horologist:horologist-compose-material:0.6.20")

// JDK 8+ desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")


    // Navigation
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.lottie.compose.v666)

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
}