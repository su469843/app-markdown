plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "io.qzz.studyhard.markdown"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.qzz.studyhard.markdown"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("releaseStoreFile") ?: "debug.keystore")
            storePassword = project.findProperty("releaseStorePassword") as String? ?: ""
            keyAlias = project.findProperty("releaseKeyAlias") as String? ?: ""
            keyPassword = project.findProperty("releaseKeyPassword") as String? ?: ""
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.markwon)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}