-dontwarn
-dontnote
-dontoptimize

-injars build/libs/breakthemod-1.4.3.jar
-outjars build/libs/btm-obf.jar

-libraryjars <java.home>/jmods/

-keepclassmembers class * {
    @net.fabricmc.api.** *;
}
-keep interface net.fabricmc.api.ClientModInitializer { *; }

-keep public class net.chariskar.breakthemod.Breakthemod { *; }

-keep class net.chariskar.breakthemod.client.commands.** implements net.chariskar.breakthemod.client.api.* { *; }
-keep class * implements net.fabricmc.api.ClientModInitializer { *; }
-keep public class net.chariskar.breakthemod.client.api.** { *; }
-keep public class net.chariskar.breakthemod.client.hooks.** { *; }

-keep class net.chariskar.breakthemod.client.utils.** { *; }
-keep public class net.chariskar.breakthemod.mixins.** { *; }

-keepclassmembers class kotlin.Metadata { *; }
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations

-keepdirectories META-INF/**
-keepdirectories resources/**