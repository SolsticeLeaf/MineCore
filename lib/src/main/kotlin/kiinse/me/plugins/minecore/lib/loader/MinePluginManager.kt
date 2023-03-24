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
package kiinse.me.plugins.minecore.lib.loader

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.exceptions.PluginException
import kiinse.me.plugins.minecore.api.loader.PluginManager
import org.bukkit.Bukkit

class MinePluginManager(mainPlugin: MineCorePlugin) : PluginManager {

    private val plugins: MutableSet<MineCorePlugin> = HashSet()
    private val mainPlugin: MineCorePlugin

    init {
        this.mainPlugin = mainPlugin
    }

    override fun hasPlugin(plugin: MineCorePlugin): Boolean {
        return hasPlugin(plugin.name)
    }

    override fun hasPlugin(plugin: String): Boolean {
        plugins.forEach { if (it.name == plugin) return true }
        return false
    }

    override val pluginsList: Set<MineCorePlugin>
        get() = HashSet(plugins)

    private fun getPlugin(plugin: String): MineCorePlugin? {
        plugins.forEach { if (it.name == plugin) return it }
        return null
    }

    @Throws(PluginException::class)
    private fun remove(plugin: MineCorePlugin) {
        remove(plugin.name)
    }

    @Throws(PluginException::class)
    private fun remove(plugin: String) {
        plugins.forEach {
            if (it.name == plugin) {
                try {
                    it.onStop()
                    remove(it)
                } catch (e: Exception) {
                    throw PluginException(e)
                }
            }
        }
    }

    override fun registerPlugin(plugin: MineCorePlugin) {
        if (!hasPlugin(plugin)) {
            plugins.add(plugin)
            mainPlugin.sendLog("Plugin '&b${plugin.name}&a' registered")
            return
        }
        mainPlugin.sendLog("Plugin '&b${plugin.name}&a' already registered! Skipping register...")
    }

    @Throws(PluginException::class)
    override fun unregisterPlugin(plugin: MineCorePlugin) {
        if (!hasPlugin(plugin)) throw PluginException("This plugin '${plugin.name}' not registered!")
        remove(plugin)
        mainPlugin.sendLog("Plugin '&b${plugin.name}&a' unregistered")
    }

    @Throws(PluginException::class)
    override fun enablePlugin(plugin: String) {
        val plug = getPlugin(plugin)
        if (plug == null || !hasPlugin(plugin)) throw PluginException("This plugin '$plugin' not found!")
        if (Bukkit.getPluginManager().isPluginEnabled(plugin)) throw PluginException("This plugin '$plugin' already enabled!")
        Bukkit.getPluginManager().enablePlugin(plug)
        mainPlugin.sendLog("Plugin '&b${plug.name}&a' started")
    }

    @Throws(PluginException::class)
    override fun disablePlugin(plugin: String) {
        val plug = getPlugin(plugin)
        if (plug == null || !hasPlugin(plugin)) throw PluginException("This plugin '$plugin' not found!")
        if (!Bukkit.getPluginManager().isPluginEnabled(plugin)) throw PluginException("This plugin '$plugin' already disabled!")
        Bukkit.getPluginManager().disablePlugin(plug)
        mainPlugin.sendLog("Plugin '&b${plug.name}&a' stopped")
    }

    @Throws(PluginException::class)
    override fun reloadPlugin(plugin: String) {
        val plug = getPlugin(plugin)
        if (plug == null || !hasPlugin(plugin)) throw PluginException("This plugin '$plugin' not found!")
        plug.restart()
        mainPlugin.sendLog("Plugin '&b${plug.name}&a' restarted")
    }
}