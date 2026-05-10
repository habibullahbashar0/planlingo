# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# Keep Compose related classes
-keep class androidx.compose.** { *; }

# Keep model classes
-keep class com.luminaos.launcher.data.model.** { *; }

# Keep Room entities
-keep class com.luminaos.launcher.data.local.entity.** { *; }

# Logging for debug builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
