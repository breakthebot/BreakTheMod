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
import net.chariskar.breakthemod.client.api.BaseModule
import net.chariskar.breakthemod.client.models.Notification
import net.chariskar.breakthemod.client.models.NotificationTypes

object NotificationManager : BaseModule("NotificationManager", "Manages all of the mod notifications.", true) {

    val notifications: HashSet<Notification> = hashSetOf()

    override fun enable() {
        if (version.contains("ALPHA")) {
            val alphaNotification = Notification(
                "Alpha",
                NotificationTypes.UsingAlpha
            )
            notifications.add(alphaNotification)
        }

        if (version.contains("BETA")) {
            val betaNotification = Notification(
                "Beta",
                NotificationTypes.UsingBeta
            )
            notifications.add(betaNotification)
        }
    }
}
