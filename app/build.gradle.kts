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
    
    // 添加产品风味配置
    flavorDimensions += "abi"
    productFlavors {
        create("universal") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            }
        }
        create("armeabiV7a") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("armeabi-v7a")
            }
        }
        create("arm64V8a") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("arm64-v8a")
            }
        }
        create("x86") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("x86")
            }
        }
        create("x86_64") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("x86_64")
            }
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
    implementation("com.github.CSAbhiOnline:AutoUpdater:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}