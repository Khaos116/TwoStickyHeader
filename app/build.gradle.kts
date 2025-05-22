plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
}

android {
  namespace = "com.two.stikcy"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.two.stikcy"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0.0"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  buildFeatures {
    buildConfig = true
    viewBinding = true
    dataBinding = false
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(libs.base.core)
  implementation(libs.base.runtime)
  implementation(libs.base.appcompat)
  implementation(libs.base.constraintlayout)
  implementation(libs.base.recyclerview)
  implementation(libs.base.multitype)
}