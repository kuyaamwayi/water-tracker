plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.watertracker"
        minSdk = 26  // Required for java.time.LocalDate without desugaring
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // Glance for App Widgets
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")

    // DataStore for local storage
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // FIX: Use CoroutineWorker so WorkManager supports suspend functions
    // work-runtime-ktx is correct — keep it
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // FIX: AppCompat needed for Theme.AppCompat used in manifest
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Startup (required for WorkManager auto-init declared in manifest)
    implementation("androidx.startup:startup-runtime:1.1.1")
}
