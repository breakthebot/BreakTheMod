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

import net.chariskar.breakthemod.client.utils.Config

/**
 * @property name Module name.
 * @property description Description.
 * */
abstract class Module : Base() {
    var enabled: Boolean = false

    fun launch() {
        if (enabled) return
        try {
            enable()
            enabled = true
        } catch (e: Exception) {
            logger.error("Error encountered when enabling $name.")
            if (Config.getDevMode()) { logger.error("Error ${e.message}") }
            enabled = false
        }
    }

    open fun disable() {
        enabled = false
    }

    protected abstract fun enable()

    open fun getModuleDescription() = "$name: $description"
}