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
package kiinse.me.plugins.minecore.plugin.commands.statistic

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.commands.MineCommand
import kiinse.me.plugins.minecore.api.commands.CommandContext
import kiinse.me.plugins.minecore.api.commands.MineCoreCommand
import kiinse.me.plugins.minecore.api.files.messages.Message
import kiinse.me.plugins.minecore.api.files.messages.MessagesUtils
import kiinse.me.plugins.minecore.api.files.statistic.StatisticManager
import kiinse.me.plugins.minecore.plugin.files.Replace
import kiinse.me.plugins.minecore.lib.files.messages.MineMessagesUtils
import kiinse.me.plugins.minecore.lib.utilities.MinePlayerUtils
import kiinse.me.plugins.minecore.lib.utilities.MineUtils

class StatisticCommands(plugin: MineCorePlugin) : MineCoreCommand(plugin) {

    private val messagesUtils: MessagesUtils = MineMessagesUtils(plugin)
    private val minecoreStatistic: StatisticManager = plugin.mineCore.mineStatistic

    @MineCommand(command = "statistic", permission = "minecore.statistic", disallowNonPlayer = true)
    fun command(context: CommandContext) {
        val sender = context.sender
        val stats = minecoreStatistic.getPlayerStatistic(MinePlayerUtils.getPlayer(sender)).allStatistic
        val msg = if (stats.isNotEmpty()) {
            val message = StringBuilder()
            stats.forEach { (entityType, i) -> message.append(entityType.toString()).append("&b: &a").append(i).append("\n") }
            message.toString()
        } else {
            "&a&lN/A"
        }
        messagesUtils.sendMessage(sender, Message.STATISTIC, Replace.STATISTIC, MineUtils.colorize(msg))
    }
}