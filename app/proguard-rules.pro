# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\sdk/tools/proguard/proguard-android.txt
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
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-keepattributes *Annotation*

-allowaccessmodification
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable
-repackageclasses ''

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**

#AppCompat V7
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
#complilesdk version 23
-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-keep public class android.webkit.**
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }
#android application
-dontnote com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#javax
-keep public class javax.**

#google analytics
-keep class com.google.android.gms.analytics.**
-keep class com.google.analytics.tracking.**
-dontwarn com.google.android.gms.analytics.**
-dontwarn com.google.analytics.tracking.**
-dontwarn com.google.android.maps.**

-keep  class vi.com.** { *; }
#iflytek voice
-keep class com.iflytek.**
-keep  class com.iflytek.** { *; }
#baidu share
-dontwarn com.baidu.**
-keep class com.baidu.**
-keep  class com.baidu.** { *; }
-keep  class com.baidu.frontia.** { *; }
#slidingmenu
-keep class com.airwen.library.lib.**
-keep public class * extends com.airwen.library.lib.app.SlidingFragmentActivity
#umeng push and share
#-dontshrink
#-dontoptimize
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

#-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

#-keep public class **.R$*{ public static final int *;}

#alipay
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

-keep class com.umeng.message.protobuffer.*{
    public <fieds>;
    public <methods>;
}
-keep class com.umeng.message.*{
    public <fields>;
    public <methods>;
}

-keep class com.umeng.socialize.*{*;}

-keep class org.android.aggo.impl.*{
    public <fields>;
    public <methods>;
}
-keep class org.android.agoo.service.*{ *;}
-keep class org.android.spdy.**{*;}

#tencent weibo
-dontwarn com.tencent.weibo.sdk.**
-keep public interface com.tencent.**
-keep public class com.tencent.**{*;}

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage{*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject{*;}

#yixing
-keep class im.yixin.sdk.api.YXMessage{*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

#facebook
-dontwarn com.facebook.**
-keep enum com.facebook.**
-keep public interface com.facebook.**
-keep public class com.facebook.**

#google gson proguard setting
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

-keepclassmembers class * implements java.io.Serializable {
 	static final long serialVersionUID;
 	private static final java.io.ObjectStreamField[] serialPersistentFields;
 	private void writeObject(java.io.ObjectOutputStream);
 	private void readObject(java.io.ObjectInputStream);
 	java.lang.Object writeReplace();
 	java.lang.Object readResolve();
 }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * extends android.app.Activity {
	public void *(android.view.View);
}

-keepclassmembers class * extends java.lang.Thread {
	public void *();
}

-keepclassmembers class * implements java.lang.Runnable {
	public void *();
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * {
	public protected *;
	}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#保留不混淆的POJO for gson reflect
-keep class com.airwen.plate.bean.** { *;}