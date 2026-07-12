/*
 * This file is part of breakthemod.
 *
 * breakthemod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemod. If not, see <https://www.gnu.org/licenses/>.
 */

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