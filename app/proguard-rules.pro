# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keep interface com.kakao.sdk.**.*Api
# Keep Gson model classes
-keep class com.depromeet.team6.data.dataremote.model.** { *; }
-keep class com.depromeet.team6.data.datalocal.model.** { *; }
-keep class com.depromeet.team6.domain.model.** { *; }
-keep class com.depromeet.team6.presentation.model.** { *; }

-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking interface retrofit2.Call

-dontwarn javax.annotation.**
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
-dontwarn com.codehaus.mojo.animal_sniffer.*
-dontwarn org.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn com.google.flatbuffers.Struct
-dontwarn com.google.flatbuffers.Table

# Keep annotations
-keepattributes *Annotation*

# Optional: if you're using Retrofit + Gson
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class okhttp3.** { *; }

# TimeLeftService 관련 클래스 보존
-keep class com.depromeet.team6.**.TimeLeftService { *; }

# Hilt 모듈에 정의된 provides 메서드 클래스 보존
-keep class com.depromeet.team6.di.ServiceModule { *; }

# 모든 @Provides, @Inject 대상 보존
-keepclasseswithmembers class * {
    @dagger.** *;
    @javax.inject.** *;
}