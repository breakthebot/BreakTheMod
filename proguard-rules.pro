# proguard-rules.pro (UPDATED)
-verbose

# ✅ Enable optimization
-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# ✅ Enable aggressive obfuscation
-obfuscationdictionary obfuscation-dict.txt
-classobfuscationdictionary obfuscation-dict.txt

-dontwarn kotlin.**
-dontwarn org.jetbrains.**
-dontwarn kotlinx.**

-keepclassmembers class * {
    @net.fabricmc.api.** *;
}
-keep interface net.fabricmc.api.** { *; }
-keep class net.fabricmc.** { *; }

-keep public class net.chariskar.breakthemod.Breakthemod { *; }
-keep public class net.chariskar.breakthemod.client.** { *; }

-keepclassmembers class * extends net.chariskar.breakthemod.client.api.BaseCommand {
    public static *** INSTANCE;
}
-keepclassmembers class * extends net.chariskar.breakthemod.client.api.Module {
    public static *** INSTANCE;
}

-keep public class org.breakthebot.breakthelibrary.** { *; }
-keep public class org.breakthebot.breakthelibrary.models.** { *; }

-keepclassmembers class kotlin.Metadata { *; }
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,Signature,InnerClasses,EnclosingMethod

-keepclassmembers class **$Serializer { *; }

-keepdirectories META-INF/**