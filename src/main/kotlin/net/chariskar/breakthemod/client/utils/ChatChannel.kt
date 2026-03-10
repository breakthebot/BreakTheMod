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

//// credit to https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/object/wrapper/ChatChannel.java

package net.chariskar.breakthemod.client.utils

class ChatChannel(val name: String?, val colour: Int) {
    companion object {
        val GLOBAL: ChatChannel = ChatChannel("global", -0x555556)
        val TOWN: ChatChannel = ChatChannel("town", -0xaa0001)
        val NATION: ChatChannel = ChatChannel("nation", -0xab)
        val LOCAL: ChatChannel = ChatChannel("local", -0xa4158e)
        val STAFF: ChatChannel = ChatChannel("staff", -0x580000)
        val TRADE: ChatChannel = ChatChannel("trade", -0xaa0001)
        val PREMIUM: ChatChannel = ChatChannel("premium", -0x3ab04)

        val PORTUGUESE: ChatChannel = ChatChannel("portuguese", -0xab03ac)
        val TURKISH: ChatChannel = ChatChannel("turkish", -0xab03ac)
        val SWEDISH: ChatChannel = ChatChannel("swedish", -0xab03ac)
        val GERMAN: ChatChannel = ChatChannel("german", -0xab03ac)
        val UKRAINIAN: ChatChannel = ChatChannel("ukrainian", -0xab03ac)
        val CHINESE: ChatChannel = ChatChannel("chinese", -0xab03ac)
        val FRENCH: ChatChannel = ChatChannel("french", -0xab03ac)
        val POLISH: ChatChannel = ChatChannel("polish", -0xab03ac)
        val RUSSIAN: ChatChannel = ChatChannel("russian", -0xab03ac)
        val SPANISH: ChatChannel = ChatChannel("spanish", -0xab03ac)
        val DUTCH: ChatChannel = ChatChannel("dutch", -0xab03ac)
        val JAPANESE: ChatChannel = ChatChannel("japanese", -0xab03ac)

        val CHANNELS: MutableSet<ChatChannel> = mutableSetOf(
            GLOBAL, TOWN, NATION, LOCAL,
            STAFF, TRADE, PREMIUM, PORTUGUESE,
            TURKISH, SWEDISH, GERMAN, UKRAINIAN,
            CHINESE, FRENCH, POLISH, RUSSIAN,
            SPANISH, DUTCH, JAPANESE
        )

        fun getOrDefault(name: String?): ChatChannel {
            for (channel in CHANNELS) {
                if (channel.name == name) return channel
            }

            return ChatChannel(name, 0xaaaaaa)
        }
    }
}