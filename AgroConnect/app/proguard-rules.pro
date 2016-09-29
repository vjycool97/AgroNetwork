# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt-bundle-windows-x86-20140702\adt-bundle-windows-x86-20140702\sdk/tools/proguard/proguard-android.txt
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
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-keep public class org.codehaus.**
-keep public class java.nio.**
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }





# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt


## Square Otto specific rules ##
## https://square.github.io/otto/ ##

-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

## Square Picasso specific rules ##
## https://square.github.io/picasso/ ##

-dontwarn com.squareup.okhttp.**

#Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }


## Google Analytics 3.0 specific rules ##

-keep class com.google.analytics.** { *; }

# ButterKnife 6

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn okio.**




#======================================================#
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#-dontshrink


# http://stackoverflow.com/questions/10158849/android-proguard-return-line-number
-keepattributes SourceFile,LineNumberTable


# http://stackoverflow.com/questions/4732656/enabling-proguard-in-eclipse-for-android
#Use 5 step of optimization
#-optimizationpasses 5

#When not preverifing in a case-insensitive filing system, such as Windows. This tool will unpack your processed jars,(if using windows you should then use):
-dontusemixedcaseclassnames

#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify

-dontwarn android.support.**

#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose

#The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle. Note that the Dalvik VM also can't handle aggressive overloading (of static fields).
#To understand or change this check http://proguard.sourceforge.net/index.html#/manual/optimizations.html
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

#To repackage classes on a single package
#-repackageclasses ''

#Uncomment if using annotations to keep them.
#-keepattributes *Annotation*

#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

#Compatibility library
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
 -keepclassmembers public class * extends android.view.View {
  void set*(***);
  *** get*();
}

#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
}

#To avoid changing names of methods invoked on layout's onClick.
# Uncomment and add specific method names if using onClick on layouts
#-keepclassmembers class * {
# public void onClickButton(android.view.View);
#}

#Maintain java native methods
-keepclasseswithmembers class * {
    native <methods>;
}


#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

###### ADDITIONAL OPTIONS NOT USED NORMALLY

#To keep callback calls. Uncomment if using any
#http://proguard.sourceforge.net/index.html#/manual/examples.html#callback
#-keep class mypackage.MyCallbackClass {
#   void myCallbackMethod(java.lang.String);
#}

#Uncomment if using Serializable
#-keepclassmembers class * implements java.io.Serializable {
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}

-dontwarn **CompatHoneycomb
-dontwarn **CompatCreatorHoneycombMR2
-keep class android.support.v4.** { *; }

-dontwarn java.awt.**,javax.security.**,java.beans.**,javax.xml.**,java.util.**,org.w3c.dom.**

-dontwarn com.google.**



-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-assumenosideeffects class org.apache.log4j.Logger {
                   public static *** getLogger(...);
                   public static *** debug(...);
                   public static *** error(...);
                   public static *** fatal(...);
                   public static *** warn(...);
                }

-dontwarn org.apache.commons.logging.**


#Keep ABS
#-keep class com.actionbarsherlock.** {*;}

#keep GA and google stuff
# -keep public class com.google.** {*;}
# To prevent ProGuard from stripping away required classes, add the following lines in the
# http://developer.android.com/google/play-services/setup.html#Proguard
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class agroconnectapp.agroconnect.in.agroconnect.networkclient.model.City { *; }
-keep class agroconnectapp.agroconnect.in.agroconnect.networkclient.model.Commodity { *; }
-keep class agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User { *; }

-keepclasseswithmembernames public class com.orm.**
-keepclasseswithmembernames public class * extends com.orm.SugarRecord
-keepclasseswithmembernames public class agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
# -keep class mypersonalclass.data.model.** { *; }
-keep class agroconnectapp.agroconnect.in.agroconnect.networkclient.model.** { *; }
