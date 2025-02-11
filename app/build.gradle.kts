plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.kapt)
	alias(libs.plugins.room)
	alias(libs.plugins.google.services)
	alias(libs.plugins.firebase.crashlytics)
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotlinx.serialization)
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 5
val versionBuild = 7

var versionBuildName = "$versionMajor.$versionMinor.$versionPatch"

android {
	namespace = "basilliyc.cashnote"
	compileSdk = 35
	
	defaultConfig {
		applicationId = "basilliyc.cashnote"
		minSdk = 29
		targetSdk = 35
		
		versionCode = versionBuild
		versionName = versionBuildName
		setProperty("archivesBaseName", "CashNote-$versionName")
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	
	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			resValue("bool", "crashlytics_enable", "true")
		}
		debug {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			resValue("bool", "crashlytics_enable", "false")
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
		compose = true
		buildConfig = true
	}
	room {
		schemaDirectory("$projectDir/db_schema")
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
	
	implementation(libs.androidx.navigation)
	implementation(libs.androidx.paging.compose)
	implementation(libs.androidx.paging.runtime)
	
	implementation(libs.di.koin)
	implementation(libs.bundles.room)
	ksp(libs.room.compiler)
	
	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.analytics)
	implementation(libs.firebase.crashlytics)
	
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.collections.immutable)
	
	implementation(libs.compose.material.icons)
	
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}