plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Coroutines
    implementation(libs.coroutines.core)

    // Serialization
    implementation(libs.kotlin.serialization)

    // Test
    testImplementation(libs.junit)
}