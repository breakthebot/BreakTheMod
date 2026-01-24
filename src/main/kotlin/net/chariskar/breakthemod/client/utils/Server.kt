package net.chariskar.breakthemod.client.utils

import net.minecraft.client.MinecraftClient
import java.util.Locale

object ServerUtils {

    /** is this emc. */
    fun isEarthMc(): Boolean {
        val serverInfo = MinecraftClient.getInstance().currentServerEntry ?: return false
        return serverInfo.address.split(",".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()[0].lowercase().contains("earthmc")
    }

    fun getEnabled(): Boolean {
        return isEarthMc() || Config.getEnabledServers()
    }
}