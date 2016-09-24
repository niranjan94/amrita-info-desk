-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable
-allowaccessmodification

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class android.support.** { *; }
-keep interface android.support.** { *; }

-keep class com.android.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes InnerClasses,EnclosingMethod

-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**

-keep class javax.net.ssl.** { *; }
-keep class okhttp3.** { *; }
-keep class com.njlabs.** { *; }

-keepattributes SourceFile,LineNumberTable

-keep public class * extends java.lang.Exception

-dontwarn com.parse.**
-dontwarn com.squareup.**
-dontwarn org.joda.**
-dontwarn com.google.**
-dontwarn com.android.**
-dontwarn com.njlabs.aid.**
-dontwarn android.test.**
-dontwarn junit.runner.**
-dontwarn com.**
-dontwarn android.**
-dontwarn org.**
-dontwarn net.**
-dontwarn uk.**
-dontwarn io.**
-dontwarn java.**
-dontwarn okhttp3.**
-dontwarn javax.net.ssl.**
-dontwarn net.fortuna.ical4j.model.**
