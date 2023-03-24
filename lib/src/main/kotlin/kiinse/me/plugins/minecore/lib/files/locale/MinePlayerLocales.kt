// MIT License
//
// Copyright (c) 2022 kiinse
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package kiinse.me.plugins.minecore.lib.files.locale

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.files.locale.LocaleStorage
import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import kiinse.me.plugins.minecore.api.files.locale.PlayerLocales
import kiinse.me.plugins.minecore.lib.utilities.MinePlayerUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.logging.Level

class MinePlayerLocales(plugin: MineCorePlugin, storage: LocaleStorage) : PlayerLocales {

    private val plugin: MineCorePlugin
    private val storage: LocaleStorage

    init {
        this.plugin = plugin
        this.storage = storage
    }

    override fun isLocalized(player: Player): Boolean {
        return storage.isLocalesDataContains(player)
    }

    override fun getLocale(player: Player): MineLocale {
        return if (isLocalized(player)) storage.getLocalesData(player)!! else storage.defaultMineLocale
    }

    override fun getLocale(sender: CommandSender): MineLocale {
        if (sender is ConsoleCommandSender) return storage.defaultMineLocale
        val player = MinePlayerUtils.getPlayer(sender)
        return if (isLocalized(player)) storage.getLocalesData(player)!! else storage.defaultMineLocale
    }

    override fun convertStringToLocale(locale: String): MineLocale {
        return MinePlayerMineLocale(locale)
    }

    override fun setLocale(player: Player, mineLocale: MineLocale) {
        if (isLocalized(player) && storage.removeLocalesData(player))
            plugin.sendLog(Level.CONFIG, "Player '${MinePlayerUtils.getPlayerName(player)}' locale has been removed")
        if (storage.putInLocalesData(player, if (storage.isAllowedLocale(mineLocale)) mineLocale else storage.defaultMineLocale))
            plugin.sendLog(Level.CONFIG, "Player '${MinePlayerUtils.getPlayerName(player)}' locale has been added. Locale: $mineLocale")
    }

    override fun getInterfaceLocale(player: Player): MineLocale {
        return convertStringToLocale(player.locale.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
    }
}