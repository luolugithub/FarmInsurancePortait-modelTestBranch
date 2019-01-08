# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/wbs/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Keep line number information, useful for stack traces.
-keepattributes SourceFile,LineNumberTable
# -repackageclasses a

-keepclasseswithmembernames class * {
    native <methods>;
}

## Amap
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

## okhttp3
-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.*
-dontwarn javax.annotation.GuardedBy
-dontwarn javax.annotation.**
-dontwarn retrofit2.Platform$Java8
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.ClassValue

## opencv
-keep class org.opencv.engine.OpenCVEngineInterface{*;}
-keep class org.opencv.**{*;}

-keep  class org.tensorflow.contrib.android.TensorFlowInferenceInterface{*;}
-keep  class org.tensorflow.lite.NativeInterpreterWrapper {*;}
##gson
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
-keep class com.innovation.** { *; }
-keep class org.tensorflow.demo.** { *; }
-keepattributes EnclosingMethod

-ignorewarnings
-keep class * {
    public private *;
}
