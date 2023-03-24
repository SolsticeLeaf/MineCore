package kiinse.me.plugins.minecore.api.commands

import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
interface CommandContext {
    val sender: CommandSender
    val args: Array<String>
    val senderLocale: MineLocale
}