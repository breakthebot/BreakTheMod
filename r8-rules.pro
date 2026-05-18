-dontobfuscate
-dontoptimize

-dontwarn net.minecraft.**
-dontwarn net.fabricmc.**
-dontwarn org.spongepowered.**
-dontwarn com.mojang.**
-dontwarn net.fabricmc.loader.**
-dontwarn me.shedaniel.**
-dontwarn com.terraformersmc.**
-dontwarn eu.pb4.**

-keep class net.chariskar.breakthemod.api.** { *; }

-keep class net.chariskar.breakthemod.** {
    public *;
}

-keep class net.chariskar.breakthemod {
    public *;
}

-keep class org.breakthebot.breakthelibrary.** {
    public *;
}

-keep class kotlin.Metadata { *; }

-keepattributes *Annotation*