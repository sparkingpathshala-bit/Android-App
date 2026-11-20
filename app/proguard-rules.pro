# 1. Reflection, Annotation & Stack Trace De-obfuscation Attributes
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# 2. Retrofit & Coroutines
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep interface com.sparkingpathshala.app.data.remote.ApiService { <methods>; }

# 3. Room Database & DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * {
    <fields>;
    <init>(...);
}

# 4. Network DTOs & Serialization (Fine-grained to maximize R8 optimization & tree shaking)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers enum * { *; }

# Allow R8 to shrink and obfuscate DTO classes and unused methods while preserving constructors for Gson reflection
-keepclassmembers,allowobfuscation class com.sparkingpathshala.app.data.remote.** {
    <init>(...);
}

# 5. ViewModel Constructors
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# 6. Jetpack Security & Google Tink Crypto
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn com.google.api.client.http.**
-dontwarn org.joda.time.**
-keep class com.google.crypto.tink.** { *; }
-keep class androidx.security.crypto.** { *; }

# 7. Strip Debug / Verbose Logging in Release Builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# 8. Repackage Obfuscated Classes for Tighter DEX Structure
-repackageclasses 'com.sparkingpathshala.app.a'
-allowaccessmodification

# 9. Preserve BuildConfig Constants
-keep class com.sparkingpathshala.app.BuildConfig { *; }



