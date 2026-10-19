plugins { id("com.android.application") }

android {
    namespace = "com.hammad.rmtweaks"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.hammad.rmtweaks"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
    signingConfigs {
        create("shared") {
            storeFile = file("rmtweaks.jks")
            storePassword = System.getenv("RMTWEAKS_STORE_PASSWORD") ?: "rmtweaks"
            keyAlias = System.getenv("RMTWEAKS_KEY_ALIAS") ?: "rmtweaks"
            keyPassword = System.getenv("RMTWEAKS_KEY_PASSWORD") ?: "rmtweaks"
        }
    }
    buildTypes {
        debug { signingConfig = signingConfigs.getByName("shared") }
        release { isMinifyEnabled = false; signingConfig = signingConfigs.getByName("shared") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { buildConfig = true }
    lint { abortOnError = false }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.documentfile:documentfile:1.0.1")
    compileOnly("de.robv.android.xposed:api:82")
}
