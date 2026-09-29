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

package net.chariskar.breakthemod.client.models

enum class NotificationTypes {
    UsingBeta,
    UsingAlpha,
    UsingDebug,
    UpdateAvailable,
}

/**
 * Represents a notification from the mod.
 * @property notificationType The type of the notification.
 * */
data class Notification(
    val name: String,
    val notificationType: NotificationTypes,
) {

    fun getMessage() = when (notificationType) {
        NotificationTypes.UsingBeta -> "You are using a beta version of breakthemod."
        NotificationTypes.UsingAlpha -> "You are using a alpha version of breakthemod."
        NotificationTypes.UsingDebug -> "The breakthemod debug toolkit is active."
        NotificationTypes.UpdateAvailable -> "There is a new breakthemod version available."
    }
}
