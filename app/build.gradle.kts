import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-parcelize")
    alias(libs.plugins.android.navigation.safeargs)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "br.com.caiorodri.agenpet"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.caiorodri.agenpet"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {

            applicationIdSuffix = ".dev"

            resValue("string", "app_name", "AgenPet Dev")

            buildConfigField("String", "API_URL", "\"http://192.168.0.8:8080/\"")
            buildConfigField("String", "API_NAME", "\"agendamento-veterinario\"")
            buildConfigField("int", "PAGINA_PADRAO", "0")
            buildConfigField("int", "QUANTIDADE_ITENS_CONSULTA", "10")

            isDebuggable = true

        }

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_name", "AgenPet")

            signingConfig = signingConfigs.getByName("debug")

            val apiUrl = localProperties.getProperty("API_URL", "\"URL_NAO_DEFINIDA_NO_LOCAL_PROPERTIES\"")
            val apiName = localProperties.getProperty("API_NAME", "\"NOME_API_NAO_DEFINIDO\"")

            buildConfigField("String", "API_URL", apiUrl)
            buildConfigField("String", "API_NAME", apiName)
            buildConfigField("int", "PAGINA_PADRAO", "0")
            buildConfigField("int", "QUANTIDADE_ITENS_CONSULTA", "10")
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
        buildConfig = true
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
}