-keepclassmembers class * {
@android.webkit.JavascriptInterface <methods> ;
}

-keepattributes JavascriptInterface
-keepattributes Annotation

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
public void onPayment*(...);
}
-keep class com.google.android.gms.* { *; }
-keep class com.facebook.applinks.** { *; }
-keepclassmembers class com.facebook.applinks.** { *; }
-keep class com.facebook.FacebookSdk { *; }
-assumenoexternalsideeffects class android.util.Log{
public static *** d(...);
public static *** v(...);
public static *** i(...);
public static *** d(...);
}