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

package net.chariskar.breakthemod.client.api.providers

import net.chariskar.breakthemod.Breakthemod.Companion.logger
import net.chariskar.breakthemod.client.utils.Config

interface LoggingProvider {

    fun logError(message: String, e: Exception) = logger.error("$message: ${e.message}", e)

    fun logDebug(message: String) {
        if (Config.getDbg()) {
            logger.info("[DEBUG] $message")
        }
    }
}