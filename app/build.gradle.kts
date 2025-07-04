plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "it.codesmell.yahtzee"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.codesmell.yahtzee"
        minSdk = 30
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.common.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")
    implementation("androidx.compose.material3:material3:1.4.0-alpha14")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.foundation:foundation:1.5.4")
    //implementation ("androidx.room:room-runtime:2.6.1") // Per Coroutine support
    implementation ("androidx.room:room-ktx:2.6.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    ksp("androidx.room:room-compiler:2.6.1") // <---- QUESTO DIO CRISTO


}