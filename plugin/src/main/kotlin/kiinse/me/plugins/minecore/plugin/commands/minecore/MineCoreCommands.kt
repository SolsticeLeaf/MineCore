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
package kiinse.me.plugins.minecore.plugin.commands.minecore

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.commands.CommandContext
import kiinse.me.plugins.minecore.api.commands.MineCommand
import kiinse.me.plugins.minecore.api.commands.MineCoreCommand
import kiinse.me.plugins.minecore.api.commands.MineSubCommand
import kiinse.me.plugins.minecore.api.files.messages.Message
import kiinse.me.plugins.minecore.api.files.messages.MessagesUtils
import kiinse.me.plugins.minecore.api.loader.PluginManager
import kiinse.me.plugins.minecore.plugin.files.Replace
import kiinse.me.plugins.minecore.lib.files.messages.MineMessagesUtils
import kiinse.me.plugins.minecore.lib.utilities.MinePlayerUtils
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import java.util.logging.Level

@Suppress("unused")
@MineCommand(command = "minecore")
class MineCoreCommands(plugin: MineCorePlugin) : MineCoreCommand(plugin) {

    private val pluginManager: PluginManager
    private val messagesUtils: MessagesUtils

    init {
        pluginManager = plugin.mineCore.pluginManager
        messagesUtils = MineMessagesUtils(plugin)
    }

    @MineSubCommand(command = "reload", permission = "minecore.reload", parameters = 1)
    fun reload(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        if (hasPlugin(sender, args[0])) {
            try {
                pluginManager.reloadPlugin(args[0])
                messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_RELOADED, Replace.PLUGIN, args[0])
                MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
            } catch (e: Exception) {
                messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_ERROR)
                plugin.sendLog(Level.SEVERE, "Error on plugin '${args[0]}' reload! Message:", e)
            }
        }
    }

    @MineSubCommand(command = "enable", permission = "minecore.enable", parameters = 1)
    fun enable(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        if (hasPlugin(sender, args[0])) {
            try {
                pluginManager.enablePlugin(args[0])
                messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_ENABLED, Replace.PLUGIN, args[0])
                MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
            } catch (e: Exception) {
                messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_ERROR)
                plugin.sendLog(Level.SEVERE, "Error on plugin '${args[0]}' enable! Message:", e)
            }
        }
    }

    @MineSubCommand(command = "disable", permission = "minecore.disable", parameters = 1)
    fun disable(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        if (hasPlugin(sender, args[0])) {
            try {
                pluginManager.disablePlugin(args[0])
                messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_DISABLED, Replace.PLUGIN, args[0])
                MinePlayerUtils.playSound(sender, Sound.BLOCK_AMETHYST_BLOCK_HIT)
            } catch (e: Exception) {
                messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_ERROR)
                plugin.sendLog(Level.SEVERE, "Error on plugin '${args[0]}' disable! Message:", e)
            }
        }
    }

    @MineSubCommand(command = "test", permission = "minecore.test")
    fun test(context: CommandContext) {
        plugin.sendLog("&bTest command! " + context.sender.name + " | " + context.senderLocale)
    }

    private fun hasPlugin(sender: CommandSender, plugin: String): Boolean {
        if (!pluginManager.hasPlugin(plugin)) {
            messagesUtils.sendMessageWithPrefix(sender, Message.PLUGIN_NOT_FOUND, Replace.PLUGIN, plugin)
            MinePlayerUtils.playSound(sender, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
            return false
        }
        return true
    }
}