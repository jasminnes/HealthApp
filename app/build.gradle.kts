import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.tu.health"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.tu.health"
        minSdk = 24
        targetSdk = 36
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
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        sourceSets {
            val main by getting
            main.kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
    buildFeatures {
        compose = true
    }
    kotlin {
        jvmToolchain(17)
        compilerOptions {
            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        }
    }
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.coroutines.android)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Material Icons
    implementation(libs.androidx.material.icons.extended)

    // Accompanist
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.systemuicontroller)

    // Hilt DI
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.testing)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Moshi JSON + codegen
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.moshi.kotlin)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)

    // OkHttp (for networking)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // DataStore (Preferences)
    implementation(libs.androidx.datastore.preferences)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}