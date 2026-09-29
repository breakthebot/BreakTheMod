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

package net.chariskar.breakthemod.client.api

import io.github.oshai.kotlinlogging.KotlinLogging
import net.chariskar.breakthemod.client.api.providers.MessageProvider
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.minecraft.client.Minecraft
import org.breakthebot.breakthelibrary.models.APIResult

/**
 * Base for modules.
 * @param name Module name.
 * @param description Module description.
 * @param hidden Special property that makes the module not be shown to the user.
 * @property enabled The status of the module.
 *  */
abstract class BaseModule(
    val name: String,
    val description: String,
    val hidden: Boolean = false,
) : MessageProvider,
    ServerUtilsProvider {

    var enabled: Boolean = false
        protected set

    protected val logger = KotlinLogging.logger("breakthemod")

    fun register() {
        if (enabled) return
        try {
            enable()
            enabled = true
        } catch (e: Exception) {
            logger.error(e) { "Unexpected exception occurred while enabling." }
            enabled = false
        }
    }

    fun getModuleDescription() = "$name: $description"

    open fun disable() {
        enabled = false
    }

    protected abstract fun enable()

    fun <T> APIResult<T>.logError(): APIResult<T> = when (this) {
        is APIResult.Error -> {
            if (!this.isSevere) return this
            logger.error { "Received unexpected response from the api with status code ${this.statusCode} and message ${this.message}." }
            this
        }

        is APIResult.Success -> this
    }

    companion object {
        @JvmStatic
        protected val client: Minecraft
            get() = Minecraft.getInstance()
    }
}
