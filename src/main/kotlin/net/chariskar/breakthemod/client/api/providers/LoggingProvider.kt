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

import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.Breakthemod.Companion.logger
import org.breakthebot.breakthelibrary.models.APIResult

open class LoggingProvider(
    private val name: String,
) {
    private fun prefix(message: String): String = "[$name] $message"

    fun error(message: String, throwable: Throwable) {
        logger.error(prefix(message), throwable)
    }

    fun error(error: APIResult.Error) {
        logger.error(
            prefix(
                "Received unexpected error from the API: {status=${error.statusCode}, message=${error.message}}"
            )
        )
    }

    fun info(message: String) {
        logger.info(prefix(message))
    }

    fun debug(message: String) {
        if (Breakthemod.debug) {
            logger.debug(prefix(message))
        }
    }

    fun warn(message: String) {
        logger.warn(prefix(message))
    }

    fun <T> APIResult<T>.logError(): APIResult<T> = when (this) {
        is APIResult.Error -> {
            error(this)
            this
        }

        is APIResult.Success -> this
    }
}
