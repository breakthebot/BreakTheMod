package net.chariskar.breakthemod.client.modules

import net.chariskar.breakthemod.Breakthemod.Companion.version
import net.chariskar.breakthemod.client.api.Notification
import net.chariskar.breakthemod.client.api.NotificationTypes
import net.chariskar.breakthemod.client.api.module.BaseModule

object NotificationManager : BaseModule("NotificationManager", "Manages all of the mod notifications.", true) {

    val notifications: HashSet<Notification> = hashSetOf()

    override fun enable() {
        if (version.contains("ALPHA")) {
            val alphaNotification = Notification(
                "Alpha",
                "You are running a alpha version of breakthemod, this is not a finished build, so expect glitches and instability.",
                NotificationTypes.UsingAlpha
            )
            notifications.add(alphaNotification)
        }

        if (version.contains("BETA")) {
            val betaNotification = Notification(
                "Beta",
                "You are running a beta version of breakthemod, unexpected behaviour and glitches may occur, please report any issues.",
                NotificationTypes.UsingBeta
            )
            notifications.add(betaNotification)
        }
    }
}