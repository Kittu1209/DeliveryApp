plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.fooddeliveryapp_student"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fooddeliveryapp_student"
        minSdk = 23
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

    buildFeatures {
        viewBinding = true
    }
    dependenciesInfo {
        includeInBundle = true
        includeInApk = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.android.gms:play-services-measurement:21.3.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.razorpay:checkout:1.6.41")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.android.gms:play-services-base:18.3.0")
    implementation ("com.google.firebase:firebase-bom:32.7.0")
}}
dependencies {
    implementation(libs.cardview)
    implementation(libs.firebase.auth)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    implementation(libs.recyclerview)


}
