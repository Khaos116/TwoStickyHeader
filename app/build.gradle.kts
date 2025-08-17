import java.text.SimpleDateFormat

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
}
val mVersionName = "1.0.0"
android {
  namespace = "com.two.stikcy"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.two.stikcy"
    minSdk = 24
    //noinspection OldTargetApi
    targetSdk = 34
    versionCode = mVersionName.replace(".", "").toInt()
    versionName = mVersionName
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  //https://github.com/owntracks/android/blob/43db0ad8428fa30e3edb1e27c9c08143e3e81693/project/app/build.gradle.kts
  signingConfigs {
    register("release") {
      storeFile = File("${rootDir}/com_ab.jks")
      storePassword = "com_cc"
      keyAlias = "com_cc"
      keyPassword = "com_cc"
      enableV1Signing = true
      enableV2Signing = true
      enableV3Signing = true
      enableV4Signing = true
    }
  }

  buildFeatures {
    buildConfig = true
    viewBinding = true
    dataBinding = false
  }

  buildTypes {
    debug {
      signingConfig = signingConfigs.findByName("release")
      isShrinkResources = false //是否移除无用资源
      isMinifyEnabled = false //是否开启混淆
      applicationIdSuffix = ".debug"
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    release {
      signingConfig = signingConfigs.findByName("release")
      isShrinkResources = true //是否移除无用资源
      isMinifyEnabled = true //是否开启混淆
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "../proguard/my-proguard-rules.pro")
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

//<editor-fold defaultstate="collapsed" desc="打包处理">
//打包处理 https://github.com/Xposed-Modules-Repo/mufanc.tools.applock/blob/5507e105cb4fc30667f5b9d78c0eecf5348fd732/app/build.gradle.kts#L79
android.applicationVariants.all { //这里会走"渠道数"x2(Debug+Release)的次数
  outputs.all {
    //正式版还是测试版
    val apkBuildType = buildType.name.replaceFirstChar { it.uppercase() }
    //打包完成后执行APK复制到指定位置
    assembleProvider.get().doLast {
      //使用ApkParser库解析APK文件的清单信息
      val apkName = "TwoStickyHeader"
      val apkVersion = mVersionName
      val buildEndTime = SimpleDateFormat("yyyyMMdd_HHmm").format(System.currentTimeMillis())
      val apkFileName = "${apkName}_${apkBuildType}_${apkVersion}_${buildEndTime}.apk"
      val destDir = if ("Debug" == apkBuildType) {
        File(rootDir, "APK/${apkBuildType}").also {
          if (!it.exists()) it.mkdirs()
          com.android.utils.FileUtils.deleteDirectoryContents(it)
        }
      } else {
        File(rootDir, "APK/${apkBuildType}").also { if (!it.exists()) it.mkdirs() }
      }
      outputFile.copyTo(File(destDir, apkFileName), true)
    }
  }
}
//</editor-fold>

dependencies {
  implementation(libs.base.core)
  implementation(libs.base.runtime)
  implementation(libs.base.appcompat)
  implementation(libs.base.constraintlayout)
  implementation(libs.base.recyclerview)
  implementation(libs.base.multitype)
  implementation(libs.supertextview)
}