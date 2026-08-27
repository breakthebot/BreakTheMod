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

open class LoggingProvider(private val name: String) {

    fun error(message: String, e: Throwable) {
        logger.error("[$name] $message: ${e.message}", e)
        e.printStackTrace(System.err)
    }

    fun error(e: APIResult.Error) {
        logger.error("[$name] Received unexpected error from the api, with status code ${e.statusCode} and message ${e.message}.")
    }

    fun info(message: String) {
        logger.info("[$name] $message")
    }

    fun debug(message: String) {
        if (Breakthemod.debug) {
            logger.debug("[$name] $message")
        }
    }

    fun warn(message: String) = logger.warn("[$name] $message")

    fun <T> APIResult<T>.error(): APIResult<T> = when (this) {
        is APIResult.Error -> {
            this@LoggingProvider.error(this)
            this
        }

        is APIResult.Success -> this
    }
}
