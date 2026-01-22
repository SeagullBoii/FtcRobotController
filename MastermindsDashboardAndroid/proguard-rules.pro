# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep Kotlin reflect
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Keep inner classes
-keepattributes InnerClasses
-keep class kotlinx.coroutines.internal.** { *; }

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Don't obfuscate Kotlin inner classes
-keep class **$Companion { *; }
-keep class **$* { *; }