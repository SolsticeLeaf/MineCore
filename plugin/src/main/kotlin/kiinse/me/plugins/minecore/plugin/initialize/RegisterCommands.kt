package kiinse.me.plugins.minecore.plugin.initialize

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.plugin.commands.locale.LocaleCommands
import kiinse.me.plugins.minecore.plugin.commands.locale.LocaleTab
import kiinse.me.plugins.minecore.plugin.commands.statistic.StatisticCommands

class RegisterCommands(plugin: MineCorePlugin) {
    init {
        plugin.sendLog("Registering commands...")
        kiinse.me.plugins.minecore.lib.commands.MineCommandManager(plugin)
                .registerCommand(LocaleCommands(plugin))
                .registerCommand(kiinse.me.plugins.minecore.plugin.commands.minecore.MineCoreCommands(plugin))
                .registerCommand(StatisticCommands(plugin))
        plugin.getCommand("locale")!!.tabCompleter = LocaleTab(plugin)
        plugin.getCommand("minecore")!!.tabCompleter = kiinse.me.plugins.minecore.plugin.commands.minecore.MineCoreTab(plugin)
        plugin.sendLog("Commands registered")
    }
}