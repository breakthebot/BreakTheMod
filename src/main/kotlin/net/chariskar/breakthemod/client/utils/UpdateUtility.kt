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

package net.chariskar.breakthemod.client.utils

import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.Notification
import net.chariskar.breakthemod.client.api.NotificationTypes
import net.chariskar.breakthemod.client.api.providers.MessageProvider
import net.chariskar.breakthemod.client.modules.NotificationManager
import net.minecraft.client.Minecraft
import org.breakthebot.breakthelibrary.api.APIClient

@Serializable
data class Version(
    val version: String,
    val latest: Boolean,
    val release: Boolean,
    val mcVer: String
)

@Serializable
data class VersionFile(
    val versions: List<Version>
)

object UpdateUtility : MessageProvider {

    suspend fun checkVersion() {
        val file =
            APIClient.getRequest<VersionFile>("https://raw.githubusercontent.com/breakthebot/BreakTheMod/refs/heads/master/version.json")
                .getOrNull()?.versions
        if (file == null) {
            Breakthemod.logger.warn("Version file unavailable.")
            return
        }
        val latest = file.first { it.latest }
        val verString = Breakthemod.version.split("-")[0]
        val notification = Notification(
            "UpdateAvailable",
            message = "There is a new breakthemod update available",
            notificationType = NotificationTypes.UpdateAvailable
        )

        if (
            latest.version != verString &&
            latest.release &&
            latest.mcVer == Minecraft.getInstance().launchedVersion
        ) {
            NotificationManager.notifications.add(notification)
        }
    }
}