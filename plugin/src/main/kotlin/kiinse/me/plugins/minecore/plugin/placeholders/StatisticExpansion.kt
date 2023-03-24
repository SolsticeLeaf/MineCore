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
package kiinse.me.plugins.minecore.plugin.placeholders

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.files.statistic.StatisticManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player


class StatisticExpansion(private val plugin: MineCorePlugin) : PlaceholderExpansion() {

    private val minecoreStatistic: StatisticManager = plugin.mineCore.mineStatistic

    override fun canRegister(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player?, identifier: String): String {
        return if (player == null) "" else minecoreStatistic.getPlayerStatistic(player).getStatistic(EntityType.valueOf(identifier)).toString()
    }

    override fun getIdentifier(): String {
        return "statistic"
    }

    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    override fun getVersion(): String {
        return plugin.description.version
    }
}