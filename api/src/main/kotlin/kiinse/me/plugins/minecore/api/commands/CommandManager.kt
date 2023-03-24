package kiinse.me.plugins.minecore.api.commands

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.exceptions.CommandException
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player
import java.lang.reflect.Method
import java.util.logging.Level

@Suppress("UNUSED")
abstract class CommandManager : CommandExecutor {
    protected val plugin: MineCorePlugin
    protected val failureHandler: CommandFailureHandler
    protected val registeredSubCommandTable: MutableMap<String, RegisteredCommand> = HashMap()
    protected val registeredMainCommandTable: MutableMap<String, RegisteredCommand> = HashMap()
    protected val mainCommandTable: MutableMap<MineCoreCommand, String> = HashMap()

    protected constructor(plugin: MineCorePlugin) {
        this.plugin = plugin
        failureHandler = plugin.mineCore.commandFailureHandler
    }

    protected constructor(plugin: MineCorePlugin, failureHandler: CommandFailureHandler) {
        this.plugin = plugin
        this.failureHandler = failureHandler
    }

    /**
     * Registration class commands
     *
     * @param commandClass A class that inherits from CommandClass and contains command methods
     */
    @Throws(CommandException::class)
    abstract fun registerCommand(commandClass: MineCoreCommand): CommandManager

    @Throws(CommandException::class)
    protected fun registerMainCommand(commandClass: MineCoreCommand, method: Method): String {
        val mainMineCommand = method.getAnnotation(MineCommand::class.java)
        val command = mainMineCommand.command
        register(commandClass, method, plugin.server.getPluginCommand(command), command, mainMineCommand, true)
        return command
    }

    @Throws(CommandException::class)
    protected fun registerMainCommand(mineCoreCommand: MineCoreCommand): String {
        val mainMineCommand = mineCoreCommand.javaClass.getAnnotation(MineCommand::class.java)
        val command = mainMineCommand.command
        register(mineCoreCommand, plugin.server.getPluginCommand(command), mainMineCommand)
        return command
    }

    @Throws(CommandException::class)
    protected fun registerSubCommand(commandClass: MineCoreCommand, method: Method) {
        val annotation = method.getAnnotation(MineSubCommand::class.java)
        val mainCommand = mainCommandTable[commandClass]
        if (annotation != null && annotation.command != mainCommand) {
            val cmd = mainCommand + " " + annotation.command
            register(commandClass, method, plugin.server.getPluginCommand(cmd), cmd, annotation, false)
        }
    }

    @Throws(CommandException::class)
    protected fun register(commandClass: MineCoreCommand, method: Method, pluginCommand: PluginCommand?,
                           command: String, annotation: Any, isMainCommand: Boolean) {
        register(pluginCommand, command)
        (if (isMainCommand) registeredMainCommandTable else registeredSubCommandTable)[command] = object : RegisteredCommand(method, commandClass, annotation) {}
        plugin.sendLog(Level.CONFIG, "Command '&d$command&6' registered!")
    }

    @Throws(CommandException::class)
    protected fun register(commandClass: MineCoreCommand, pluginCommand: PluginCommand?, annotation: MineCommand) {
        val command = annotation.command
        register(pluginCommand, command)
        registeredMainCommandTable[command] = object : RegisteredCommand(null, commandClass, annotation) {}
    }

    @Throws(CommandException::class)
    protected fun register(pluginCommand: PluginCommand?, command: String) {
        if (registeredSubCommandTable.containsKey(command) || registeredMainCommandTable.containsKey(command)) throw CommandException("Command '$command' already registered!")
        if (pluginCommand == null) throw CommandException("Unable to register command command '$command'. Did you put it in plugin.yml?")
        pluginCommand.setExecutor(this)
    }

    @Throws(CommandException::class)
    protected fun getMainCommandMethod(mineCoreCommand: Class<out MineCoreCommand?>): Method {
        mineCoreCommand.methods.forEach {
            if (it.getAnnotation(MineCommand::class.java) != null) return it
        }
        val name = mineCoreCommand.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        throw CommandException("Main command in class '${name[name.size - 1]}' not found!")
    }

    protected fun isDisAllowNonPlayer(wrapper: RegisteredCommand, sender: CommandSender, disAllowNonPlayer: Boolean): Boolean {
        if (sender !is Player && disAllowNonPlayer) {
            failureHandler.handleFailure(CommandFailReason.NOT_PLAYER, sender, wrapper)
            return true
        }
        return false
    }

    protected fun hasNotPermissions(wrapper: RegisteredCommand, sender: CommandSender, permission: String): Boolean {
        if (permission != "" && !sender.hasPermission(permission)) {
            failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, wrapper)
            return true
        }
        return false
    }

    override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<String>): Boolean {
        return onExecute(sender, command, label, args)
    }

    protected abstract fun onExecute(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<String>): Boolean
}