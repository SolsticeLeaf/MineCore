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
package kiinse.me.plugins.minecore.plugin.gui

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import kiinse.me.plugins.minecore.api.files.messages.Message
import kiinse.me.plugins.minecore.api.gui.GuiAction
import kiinse.me.plugins.minecore.plugin.gui.items.CurrentPageItem
import kiinse.me.plugins.minecore.plugin.gui.items.NextPageItem
import kiinse.me.plugins.minecore.plugin.gui.items.ExitItem
import kiinse.me.plugins.minecore.plugin.gui.items.LocaleItem
import kiinse.me.plugins.minecore.plugin.gui.items.PreviousPageItem
import kiinse.me.plugins.minecore.lib.gui.MineGUI
import kiinse.me.plugins.minecore.lib.utilities.MinePlayerUtils
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.*

class LocaleGUI(plugin: MineCorePlugin) : MineGUI(plugin) {

    private var page = 0

    override fun onInventoryCreate(plugin: MineCorePlugin) {
        val locales: Map<Int, HashSet<MineLocale>> = guiPages
        addItems(locales)
                .setPreviousPageItem(locales)
                .setCurrentPageItem(locales)
                .setNextPageItem(locales)
                .setExitItem()
    }

    fun setPage(page: Int): MineGUI {
        this.page = page
        return this
    }

    private fun addItems(locales: Map<Int, HashSet<MineLocale>>): LocaleGUI {
        var position = 9
        locales[page]!!.forEach{
            val item: ItemStack = localeFlags!!.getFlag(it)
            val list = ArrayList<String>()
            val meta = item.itemMeta
            list.add(getPlugin().getMessages().getStringMessage(it, Message.SET_THIS_LOCALE))
            if (meta != null) {
                meta.lore = list
                item.itemMeta = meta
            }
            setItem(LocaleItem(position, "&f$it", item, object : GuiAction {
                override fun click(clickType: ClickType, player: Player) {
                    player.performCommand("locale set $it")
                    delete()
                }
            }))
            position++
        }
        return this
    }

    private fun setPreviousPageItem(locales: Map<Int, HashSet<MineLocale>>): LocaleGUI {
        if (locales.containsKey(page - 1)) {
            setItem(PreviousPageItem(getPlugin(), mineLocale!!, object : GuiAction {
                override fun click(clickType: ClickType, player: Player) {
                    delete()
                    MinePlayerUtils.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_STEP)
                    val localeGui = LocaleGUI(getPlugin()).setPage(page - 1)
                    localeGui.name = name
                    localeGui.mineLocale = mineLocale
                    localeGui.open(player)
                }
            }))
        }
        return this
    }

    private fun setCurrentPageItem(locales: Map<Int, HashSet<MineLocale>>): LocaleGUI {
        if (locales.size > 1) setItem(CurrentPageItem(getPlugin(), mineLocale!!, page, object : GuiAction {
            override fun click(clickType: ClickType, player: Player) {
                MinePlayerUtils.playSound(player, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
            }
        }))
        return this
    }

    private fun setNextPageItem(locales: Map<Int, HashSet<MineLocale>>): LocaleGUI {
        if (locales.containsKey(page + 1)) {
            setItem(NextPageItem(getPlugin(), mineLocale!!, object : GuiAction {
                override fun click(clickType: ClickType, player: Player) {
                    delete()
                    MinePlayerUtils.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_STEP)
                    val localeGui = LocaleGUI(getPlugin()).setPage(page + 1)
                    localeGui.name = name
                    localeGui.mineLocale = mineLocale
                    localeGui.open(player)
                }
            }))
        }
        return this
    }

    private fun setExitItem(): LocaleGUI {
        setItem(ExitItem(getPlugin(), mineLocale!!, object : GuiAction {
            override fun click(clickType: ClickType, player: Player) {
                delete()
                MinePlayerUtils.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_STEP)
            }
        }))
        return this
    }

    private val guiPages: Map<Int, HashSet<MineLocale>>
        get() {
            val storage = getPlugin().mineCore.localeStorage
            val hashmap: HashMap<Int, HashSet<MineLocale>> = HashMap<Int, HashSet<MineLocale>>()
            val list: HashSet<MineLocale> = HashSet<MineLocale>()
            var size = 0
            var pageNum = 1
            for ((localesCount, locale) in storage.allowedLocalesList.withIndex()) {
                size++
                list.add(locale)
                if (size == 9 || localesCount == storage.allowedLocalesList.size - 1) {
                    hashmap[pageNum] = HashSet(list)
                    pageNum++
                    size = 0
                    list.clear()
                }
            }
            return hashmap
        }

    companion object {
        private var localeFlags: LocaleFlags? = null

        fun setLocaleFlags(lcFlags: LocaleFlags) {
            localeFlags = lcFlags
        }
    }
}