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
package kiinse.me.plugins.minecore.lib.schedulers.minecore

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.files.enums.Config
import kiinse.me.plugins.minecore.api.indicators.IndicatorManager
import kiinse.me.plugins.minecore.api.schedulers.Scheduler
import kiinse.me.plugins.minecore.api.schedulers.SchedulerData
import kiinse.me.plugins.minecore.lib.utilities.MinePlayerUtils
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit

@SchedulerData(name = "IndicatorSchedule")
class IndicatorSchedule(plugin: MineCorePlugin) : Scheduler(plugin) {

    private val indicators: IndicatorManager

    init {
        indicators = plugin.mineCore.indicatorManager
    }

    override fun canStart(): Boolean {
        return plugin.getConfiguration().getBoolean(Config.ACTIONBAR_INDICATORS) && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
    }

    override fun run() {
        Bukkit.getOnlinePlayers().forEach {
            if (MinePlayerUtils.isSurvivalAdventure(it)) MinePlayerUtils.sendActionBar(it, PlaceholderAPI.setPlaceholders(it, indicators.getIndicators()))
        }
    }
}