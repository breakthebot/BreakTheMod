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

package net.chariskar.breakthemod.Services;


import net.chariskar.breakthemod.api.Fetch;
import net.chariskar.breakthemod.utils.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class Service {
    protected static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    protected final Fetch fetch = Fetch.getInstance();

    protected void logError(String message, Exception e) {
        LOGGER.error("{}{}", message, e.getMessage());
        if (config.getInstance().isDev()) {
            e.printStackTrace();
        }
    }

}
