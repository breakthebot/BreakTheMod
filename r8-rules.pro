-keepattributes *

-keep class net.chariskar.breakthemod.** {
    public *;
}

-keep class net.chariskar.breakthemod.client.api.** {
    public *;
}

-keep, allowshrinking class net.chariskar.shadow.** {
    public *;
}

-keep class net.chariskar.breakthemod.debug.** { *; }

-dontwarn java.**

-dontobfuscate
-dontoptimize
