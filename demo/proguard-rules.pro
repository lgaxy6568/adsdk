# (1) pub
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-flattenpackagehierarchy com.umeng.analytics.util

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes Exceptions,InnerClasses
-keepattributes Deprecated
-keepattributes SourceFile,LineNumberTable

-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.backup.BackupAgentHelper
-keep class * extends android.preference.Preference
-keep class * extends android.support.v4.**
-keep class * extends androidx.**
-keep class * extends android.app.Fragment
-keep class * extends java.lang.annotation.Annotation { *; }
-keep class * extends android.widget.View{*;}
-keep class * extends android.view.View{*;}

-keepclasseswithmembernames class * {
native <methods>;
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}

-keepclassmembers enum * { *; }

-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
	static final long serialVersionUID;
	private static final java.io.ObjectStreamField[] serialPersistentFields;
	private void writeObject(java.io.ObjectOutputStream);
	private void readObject(java.io.ObjectInputStream);
	java.lang.Object writeReplace();
	java.lang.Object readResolve();
}

-keep class **.R$* { *; }
-keep class **$Properties {*;}
-dontwarn com.android.**
-keep class com.android.** {*;}

-dontwarn android.**
-keep class android.** {*;}
-keep interface android.** { *; }

-dontwarn androidx.**
-keep class androidx.** {*;}
-keep interface androidx.** { *; }

-dontwarn kotlinx.**
-keep class kotlinx.** {*;}
-keep interface kotlinx.** { *; }

-dontwarn org.**
-keep class org.** {*;}

-dontwarn io.objectbox.**
-keep class io.objectbox.** {*;}

-dontwarn javax.**
-keep class javax.** {*;}


-dontwarn cn.yq.ad.**
-keep class cn.yq.ad.** {*;}
-keep interface cn.yq.ad.** {*;}
-keep enum cn.yq.ad.** { *; }

-dontwarn com.google.**
-keep class com.google.** {*;}

-dontwarn com.squareup.**
-keep class com.squareup.** {*;}

-dontwarn okio.**
-keep class okio.** {*;}

-dontwarn okhttp3.**
-keep class okhttp3.** {*;}

-dontwarn com.bumptech.**
-keep class com.bumptech.** {*;}
-keep enum com.bumptech.** {*;}
-keep public class * implements com.bumptech.glide.module.GlideModule {*;}
-keep class * extends com.bumptech.glide.module.AppGlideModule {*;}

# 美数
# msad
-keep class com.meishu.sdk.** { *; }

# OAID
-keep class com.bun.miitmdid.** { *; }
-keep class com.bun.lib.** { *; }
-keep class com.asus.msa.** { *; }
-keep class com.huawei.hms.ads.identifier.** { *; }
-keep class com.netease.nis.sdkwrapper.** { *; }
-keep class com.samsung.android.deviceidservice.** { *; }
-keep class a.** { *; }
-keep class XI.** { *; }

#jd
-keep class com.jd.ad.** {*;}
-keep interface com.jd.ad.**

#穿山甲
-keepclassmembers class * {
    *** getContext(...);
    *** getActivity(...);
    *** getResources(...);
    *** startActivity(...);
    *** startActivityForResult(...);
    *** registerReceiver(...);
    *** unregisterReceiver(...);
    *** query(...);
    *** getType(...);
    *** insert(...);
    *** delete(...);
    *** update(...);
    *** call(...);
    *** setResult(...);
    *** startService(...);
    *** stopService(...);
    *** bindService(...);
    *** unbindService(...);
    *** requestPermissions(...);
    *** getIdentifier(...);
   }

-keep class com.bytedance.pangle.** {*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }

-keep class ms.bd.c.Pgl.**{*;}
-keep class com.bytedance.mobsec.metasec.ml.**{*;}

-keep class com.bytedance.embedapplog.** {*;}
-keep class com.bytedance.embed_dr.** {*;}

-keep class com.bykv.vk.** {*;}

-keep class com.lynx.** { *; }

-keep class com.ss.android.**{*;}

-keep class android.support.v4.app.FragmentActivity{}
-keep class androidx.fragment.app.FragmentActivity{}

-keep class android.support.v4.app.FragmentActivity{}
-keep class androidx.fragment.app.FragmentActivity{}


