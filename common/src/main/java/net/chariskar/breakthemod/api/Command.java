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

/**
 * Abstract command class.
 * @param <S> The command source stack.
 *
 */
public abstract class Command<S> {
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsageSuffix();

    public final String getUsage() { return "/" + getName() + (getUsageSuffix().isEmpty() ? "" : " " + getUsageSuffix()); }

    protected abstract int execute(CommandContext<S> ctx) throws Exception;

    protected abstract int run(CommandContext<S> ctx) throws CommandSyntaxException;

    public abstract void register(CommandDispatcher<S> dispatcher);


    public static String getConnectedServerAddress() {
        return null;
    }

    public static boolean getEnabledOnOtherServers() {
        return false;
    }
    }