import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    val keystorePath = properties["keystore.path"] as? String
    val isSigningAvailable = !keystorePath.isNullOrBlank()

    signingConfigs {
        create("release") {
            if (isSigningAvailable) {
                storeFile = if (!keystorePath.isNullOrBlank()) file(keystorePath) else null
                storePassword = properties["keystore.password"] as? String ?: ""
                keyAlias = properties["keystore.alias"] as? String ?: ""
                keyPassword = properties["key.password"] as? String ?: ""
            }
        }
    }

    namespace = "com.depromeet.team6"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.depromeet.team6"
        minSdk = 26
        targetSdk = 34
        versionCode = 9
        versionName = "1.0.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", properties["kakao.native.app.key"].toString())
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY_MANIFEST"] = properties["kakao.native.app.key.manifest"]?.toString() ?: ""
        buildConfigField("String", "TMAP_API_KEY", properties["tmap.api.key"].toString())
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", properties["release.base.url"].toString())
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "BASE_URL", properties["dev.base.url"].toString())
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
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Network
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization.converter)
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.bundles.hilt)
    kapt(libs.hilt.compiler)

    // Navigation
    implementation(libs.androidx.compose.navigation)
    implementation(libs.hilt.navigation.compose)

    // Kakao
    implementation(libs.bundles.kakao)

    // Security
    implementation(libs.androidx.security.crypto)

    // Tmap
    implementation(files("libs/tmap-sdk-1.9.aar"))
    implementation(files("libs/vsm-tmap-sdk-v2-android-1.7.23.aar"))
    implementation(libs.flatbuffers.java)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // WebView
    implementation(libs.accompanist.webview)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // View Pager
    implementation(libs.bundles.pager)

    // Timber
    implementation(libs.timber)

    // Lottie
    implementation(libs.lottie.compose)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
