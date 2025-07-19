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

package net.chariskar.breakthemod.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.chariskar.breakthemod.utils.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract command class.
 * @param <S> The command source stack.
 */
public abstract class Command<S> {
    public static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsageSuffix();

    public final String getUsage() { return "/" + getName() + (getUsageSuffix().isEmpty() ? "" : " " + getUsageSuffix()); }

    /**
     *
     * @param ctx The Command context derives off the command source provided by the loader implementation of the class.
     * @return 0 if success, 1 if error
     * @throws Exception if anything goes wrong.
     */
    protected abstract int execute(CommandContext<S> ctx) throws Exception;

    /**
     *
     * @param ctx The Command context derives off the command source provided by the loader implementation of the class.
     * @return 0 if success, 1 if error
     * @throws CommandSyntaxException If invalid syntax
     */
    protected abstract int run(CommandContext<S> ctx) throws CommandSyntaxException;

    public abstract void register(CommandDispatcher<S> dispatcher);


    public static String getConnectedServerAddress() {
        return null;
    }

    public static boolean getEnabledOnOtherServers() {
        return false;
    }

    protected void logError(String message, Exception e) {
        LOGGER.error("{}{}", message, e.getMessage());
        if (config.getInstance().isDev()) {
            e.printStackTrace();
        }
    }
}