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

# Preservation of stack trace details for deobfuscated crash reports in Play Console
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Gson serialization metadata and serialized fields
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep the Gemini/Firebase AI remote data transfer objects intact
-keep class app.aura.clckt.data.remote.** { *; }