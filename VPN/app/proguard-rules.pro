# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-assumenosideeffects class android.util.Log { *; }

-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn org.mockito.**
-dontwarn sun.reflect.**
-dontwarn android.test.**

-dontwarn javax.annotation.**
-dontwarn okio.**
-dontwarn okhttp3.internal.platform.*

-keep class org.matrixvpn.strongswan.** {*;}
-keep class org.matrixvpn.strongswan.** {*;}
-keepclassmembers  class org.matrixvpn.strongswan.** {*;}
-keepclassmembers  class org.matrixvpn.strongswan.** {*;}

#ovpn obfuscation
-keep class de.blinkt.openvpn.** {*;}
-keep class de.blinkt.openvpn.** {*;}
-keepclassmembers  class de.blinkt.openvpn.** {*;}
-keepclassmembers  class de.blinkt.openvpn.** {*;}

-keep class org.spongycastle.util.** {*;}
-keep class org.spongycastle.util.** {*;}
-keepclassmembers  class org.spongycastle.util.** {*;}
-keepclassmembers  class org.spongycastle.util.** {*;}

-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends android.app.Application

-keepattributes *Annotation*
-keep public class com.qkvpn.vpn.models.** { *; }



-keep class cn.pedant.SweetAlert.** { *; }
-keep public class org.matrixvpn.strongswan.** {
  public protected *;
}

-keep public class de.blinkt.openvpn.** {
  public protected *;
}

-keep public class org.spongycastle.util.** {
  public protected *;
}


-keepclassmembers class **.R$* {
    public static <fields>;
}



-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}



-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
