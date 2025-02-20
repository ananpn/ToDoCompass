plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")

}

android {
    namespace = "com.ToDoCompass"
    compileSdk = 34

    lint {
        checkReleaseBuilds = false
    }

    defaultConfig {
        applicationId = "com.ToDoCompass"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        //dataBinding = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"


}

dependencies {
    
    //implementation(project(":reorderable"))
    //implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    
    //implementation("codes.side:andcolorpicker:0.6.2")
    
    //implementation("com.github.alorma:compose-settings-storage-datastore:0.27.0")
    
    //implementation("androidx.compose.foundation:foundation:1.6.0-alpha07")

    implementation("com.marosseleng.android:compose-material3-datetime-pickers:0.7.2")
    implementation("com.github.skydoves:colorpicker-compose:1.0.5")
    
    implementation("com.materialkolor:material-kolor:1.2.8")
    implementation("androidx.datastore:datastore-preferences:1.0.0")



    implementation("com.github.alorma:compose-settings-ui-m3:1.0.2")

    implementation("androidx.media3:media3-common:1.3.1")
    val room_version = "2.6.1"
    val hilt_version = "2.49"
    val androidx_hilt_version = "1.2.0"
    
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.core:core-ktx:1.12.0")

    implementation("androidx.activity:activity-compose:1.8.2")

    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation:1.6.5")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    //implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.android.material:material:1.11.0")
    //lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    /*
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    */
    //Room
    //implementation("androidx.room:room-runtime:2.5.2")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    //Hilt
    annotationProcessor("androidx.hilt:hilt-compiler:$androidx_hilt_version")
    ksp("androidx.hilt:hilt-compiler:$androidx_hilt_version")
    implementation("androidx.hilt:hilt-common:$androidx_hilt_version")
    implementation("androidx.hilt:hilt-work:$androidx_hilt_version")
    implementation("com.google.dagger:hilt-android:$hilt_version")
    ksp("com.google.dagger:hilt-android-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-navigation-compose:$androidx_hilt_version")
    


    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}