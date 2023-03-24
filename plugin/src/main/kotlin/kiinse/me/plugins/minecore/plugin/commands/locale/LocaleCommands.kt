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
package kiinse.me.plugins.minecore.plugin.commands.locale

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.commands.MineCommand
import kiinse.me.plugins.minecore.api.commands.CommandContext
import kiinse.me.plugins.minecore.api.commands.MineCoreCommand
import kiinse.me.plugins.minecore.api.commands.MineSubCommand
import kiinse.me.plugins.minecore.api.files.locale.LocaleStorage
import kiinse.me.plugins.minecore.api.files.locale.PlayerLocales
import kiinse.me.plugins.minecore.api.files.messages.Message
import kiinse.me.plugins.minecore.api.files.messages.Messages
import kiinse.me.plugins.minecore.api.files.messages.MessagesUtils
import kiinse.me.plugins.minecore.plugin.files.Replace
import kiinse.me.plugins.minecore.lib.files.messages.MineMessagesUtils
import kiinse.me.plugins.minecore.lib.utilities.MinePlayerUtils
import kiinse.me.plugins.minecore.lib.utilities.MineUtils
import kiinse.me.plugins.minecore.plugin.gui.LocaleGUI
import org.bukkit.Sound
import java.util.*

@Suppress("unused")
class LocaleCommands(plugin: MineCorePlugin) : MineCoreCommand(plugin) {

    private val locales: PlayerLocales = plugin.mineCore.playerLocales
    private val storage: LocaleStorage = plugin.mineCore.localeStorage
    private val messages: Messages = plugin.getMessages()
    private val messagesUtils: MessagesUtils = MineMessagesUtils(plugin)

    @MineCommand(command = "locale", permission = "locale.status", disallowNonPlayer = true)
    fun locale(context: CommandContext) {
        val sender = context.sender
        messagesUtils.sendMessage(sender, Message.STATUS_COMMAND, Replace.LOCALE, locales.getLocale(sender).toString())
        MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
    }

    @MineSubCommand(command = "change", permission = "locale.change", disallowNonPlayer = true)
    fun change(context: CommandContext) {
        val sender = context.sender
        val senderLocale = context.senderLocale
        MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_STEP)
        val localeGui = LocaleGUI(plugin).setPage(1).setRows(4)
        localeGui.name = MineUtils.replaceWord(messages.getStringMessage(senderLocale, Message.LOCALES_GUI), Replace.LOCALE, senderLocale.toString())
        localeGui.open(sender)
    }

    @MineSubCommand(command = "help", permission = "locale.help", disallowNonPlayer = true)
    fun help(context: CommandContext) {
        val sender = context.sender
        messagesUtils.sendMessage(sender, Message.INFO_COMMAND)
        MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
    }

    @MineSubCommand(command = "list", permission = "locale.list", disallowNonPlayer = true)
    fun list(context: CommandContext) {
        val sender = context.sender
        messagesUtils.sendMessageWithPrefix(sender, Message.LOCALES_LIST, Replace.LOCALES, storage.allowedLocalesString)
        MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
    }

    @MineSubCommand(command = "set", permission = "locale.change", parameters = 1, disallowNonPlayer = true)
    fun set(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        val localeString = args[0]
        val locale = locales.convertStringToLocale(localeString.lowercase(Locale.getDefault()))
        if (localeString.isEmpty() || !storage.isAllowedLocale(locale)) {
            messagesUtils.sendMessageWithPrefix(sender, Message.LOCALE_NOT_FOUND, arrayOf(
                    "{LOCALE}:${if (args[1].isEmpty()) "NaN" else localeString.lowercase(Locale.getDefault())}",
                    "{LOCALES}:${storage.allowedLocalesString}"
            ))
            MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
        } else {
            locales.setLocale(MinePlayerUtils.getPlayer(sender), locale)
            messagesUtils.sendMessageWithPrefix(sender, Message.LOCALE_CHANGED, Replace.LOCALE, localeString.lowercase(Locale.getDefault()))
            plugin.sendLog("The player '&b${MinePlayerUtils.getPlayerName(sender)}&a' has changed his language. Now his language is: '&b${localeString.lowercase(Locale.getDefault())}&a'")
        }
    }

    @MineSubCommand(command = "get", permission = "locale.get", parameters = 1, disallowNonPlayer = true)
    operator fun get(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        val player = MinePlayerUtils.getPlayer(args[0])
        if (player == null) {
            messagesUtils.sendMessageWithPrefix(sender, Message.PLAYER_NOT_FOUND, Replace.PLAYER, args[0])
            MinePlayerUtils.playSound(sender, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
        } else {
            messagesUtils.sendMessageWithPrefix(sender, Message.GET_COMMAND, arrayOf(
                    "{PLAYER}:${args[0]}",
                    "{LOCALE}:${locales.getLocale(player)}"
            ))
            MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
        }
    }
}