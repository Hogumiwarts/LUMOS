import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.hogumiwarts.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hogumiwarts.myapplication"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "ipAddress",
            gradleLocalProperties(rootDir, providers).getProperty("ipAddress")
        )

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

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation("com.google.accompanist:accompanist-pager:0.30.1")  // 최신 버전
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1") // (선택) 아래 점 인디케이터
    implementation("com.google.android.gms:play-services-wearable:18.1.0")

    // 네비게이션
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // 로티
    implementation("com.airbnb.android:lottie-compose:6.6.6")


    implementation(files("libs/samsung-health-sensor-api-v1.3.0.aar"))

    implementation("androidx.health:health-services-client:1.0.0-beta03")
    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // ML 및 수학 라이브러리
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("org.apache.commons:commons-math3:3.6.1")// 필요한 수학 연산
    // TensorFlow Lite를 사용한다면
//    implementation("org.tensorflow:tensorflow-lite:2.8.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // 네트워크
    implementation(platform(libs.okhttp.bom))
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
}