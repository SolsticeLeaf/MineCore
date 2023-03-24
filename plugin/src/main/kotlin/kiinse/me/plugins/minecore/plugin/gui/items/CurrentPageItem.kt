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
package kiinse.me.plugins.minecore.plugin.gui.items

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import kiinse.me.plugins.minecore.api.files.messages.Message
import kiinse.me.plugins.minecore.api.gui.GuiAction
import kiinse.me.plugins.minecore.api.gui.GuiItem
import kiinse.me.plugins.minecore.plugin.files.Replace
import kiinse.me.plugins.minecore.lib.utilities.MineUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


class CurrentPageItem(val plugin: MineCorePlugin, mineLocale: MineLocale, page: Int, val action: GuiAction) : GuiItem {

    private val name: String = MineUtils.replaceWord(plugin.getMessages().getStringMessage(mineLocale, Message.GUI_CURRENT_PAGE),
                                                     Replace.PAGE,
                                                     page.toString())

    override fun slot(): Int {
        return 31
    }

    override fun itemStack(): ItemStack {
        return ItemStack(Material.NETHER_STAR)
    }

    override fun name(): String {
        return name
    }

    override fun action(): GuiAction {
        return action
    }
}