# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preservar líneas para stack traces legibles en Crashlytics / logcat.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ────────────────────────────────────────────────────────────────────────
# Retrofit 2
# ────────────────────────────────────────────────────────────────────────
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# ────────────────────────────────────────────────────────────────────────
# OkHttp / Okio
# ────────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ────────────────────────────────────────────────────────────────────────
# Gson — preservar nombres de campos de DTOs y anotaciones @SerializedName
# ────────────────────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# DTOs y modelos del proyecto: Gson los rellena por reflexión.
-keep class com.example.indecsa_v2.models.** { *; }
