package kiinse.me.plugins.minecore.lib.commands

import kiinse.me.plugins.minecore.api.commands.CommandContext
import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import org.bukkit.command.CommandSender

class MineContext(override val sender: CommandSender, override val senderLocale: MineLocale, override val args: Array<String>) : CommandContext {

    override fun equals(other: Any?): Boolean {
        return other != null && other is CommandContext && other.hashCode() == hashCode()
    }

    override fun toString(): String {
        return """
            Sender: ${sender.name}
            Locale: $senderLocale
            Args: ${args.contentToString()}
            """.trimIndent()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}