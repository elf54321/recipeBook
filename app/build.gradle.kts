plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.elte.recipebook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.elte.recipebook"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.material)
    ///Room DB
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")

    ///Navigation
    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    /// OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.compose.material)
    implementation(libs.compose.icons)
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.kizitonwose.calendar:compose:2.2.0")
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("io.github.mr0xf00:easycrop:0.1.1")

    // Live Data runtime
    implementation("androidx.compose.runtime:runtime:1.8.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.0")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.8.0")

}

kapt {
    correctErrorTypes = true
}