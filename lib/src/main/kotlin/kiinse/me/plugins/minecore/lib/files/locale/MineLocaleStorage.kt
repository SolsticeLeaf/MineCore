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
package kiinse.me.plugins.minecore.lib.files.locale

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.exceptions.JsonFileException
import kiinse.me.plugins.minecore.api.exceptions.LocaleException
import kiinse.me.plugins.minecore.api.files.enums.Config
import kiinse.me.plugins.minecore.api.files.enums.Directory
import kiinse.me.plugins.minecore.api.files.enums.File
import kiinse.me.plugins.minecore.api.files.filemanager.FilesManager
import kiinse.me.plugins.minecore.api.files.filemanager.JsonFile
import kiinse.me.plugins.minecore.api.files.locale.LocaleStorage
import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import org.bukkit.entity.Player
import org.json.JSONObject
import java.util.*
import java.util.logging.Level

class MineLocaleStorage(plugin: MineCorePlugin) : FilesManager(plugin), LocaleStorage {

    private val plugin: MineCorePlugin
    private val jsonFile: JsonFile
    private var defaultPlayerMineLocale: MineLocale? = null
    private var allowedMineLocales: Set<MineLocale>? = null
    private var locales: HashMap<UUID, MineLocale>? = null

    init {
        val directoryName = Directory.MESSAGES
        if (isFileNotExists(directoryName)) copyFile(directoryName)
        this.plugin = plugin
        jsonFile = JsonFile(plugin, File.DATA_JSON)
    }

    @Throws(LocaleException::class, JsonFileException::class)
    override fun load(): LocaleStorage {
        allowedMineLocales = parseAllowedLocales(Arrays.stream(Objects.requireNonNull(getFile(Directory.MESSAGES).listFiles())).toList())
        plugin.sendLog("Loaded locales: '&b$allowedLocalesString&a'")
        locales = parseLocalesData(jsonFile.jsonFromFile)
        val defMineLocale: MineLocale = parseDefaultLocale(plugin.getConfiguration().getString(Config.LOCALE_DEFAULT))
        if (!isAllowedLocale(defMineLocale)) throw LocaleException("This default locale '$defMineLocale' is not allowed!")
        defaultPlayerMineLocale = defMineLocale
        plugin.sendLog("Installed default locale: &b$defMineLocale")
        return this
    }

    @Throws(JsonFileException::class)
    override fun save(): LocaleStorage {
        val json = JSONObject()
        localesData.keys.forEach { json.put(it.toString(), getLocalesData(it).toString()) }
        jsonFile.saveJsonToFile(json)
        return this
    }

    override fun isAllowedLocale(mineLocale: MineLocale): Boolean {
        allowedMineLocales?.forEach { if (it == mineLocale) return true }
        return false
    }

    override fun putInLocalesData(uuid: UUID, mineLocale: MineLocale): Boolean {
        locales!![uuid] = mineLocale
        return locales!!.containsKey(uuid)
    }

    override fun putInLocalesData(player: Player, mineLocale: MineLocale): Boolean {
        val uuid = player.uniqueId
        locales!![uuid] = mineLocale
        return locales!!.containsKey(uuid)
    }

    override fun isLocalesDataContains(uuid: UUID): Boolean {
        return locales!!.containsKey(uuid)
    }

    override fun isLocalesDataContains(player: Player): Boolean {
        return locales!!.containsKey(player.uniqueId)
    }

    override fun getLocalesData(uuid: UUID): MineLocale? {
        return locales!![uuid]
    }

    override fun getLocalesData(player: Player): MineLocale? {
        return locales!![player.uniqueId]
    }

    override fun removeLocalesData(uuid: UUID): Boolean {
        locales!!.remove(uuid)
        return !locales!!.containsKey(uuid)
    }

    override fun removeLocalesData(player: Player): Boolean {
        val uuid = player.uniqueId
        locales!!.remove(uuid)
        return !locales!!.containsKey(uuid)
    }

    override val defaultMineLocale: MineLocale
        get() = defaultPlayerMineLocale!!
    override val allowedLocalesString: String
        get() = allowedMineLocales!!.joinToString(", ", "[", "]")
    override val allowedLocalesList: Set<MineLocale>
        get() = HashSet(allowedMineLocales!!)
    override val allowedLocalesListString: Set<String>
        get() {
            val set = HashSet<String>()
            allowedLocalesList.forEach { set.add(it.toString()) }
            return set
        }
    override val localesData: HashMap<UUID, MineLocale>
        get() = locales!!

    @Throws(LocaleException::class)
    private fun parseAllowedLocales(locales: MutableList<java.io.File>): Set<MineLocale> {
        val set: HashSet<MineLocale> = HashSet<MineLocale>()
        for (file in locales) {
            val fileName = file.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (fileName[1] == "json") {
                val MinePlayerLocale = MinePlayerMineLocale(fileName[0])
                if (!isContainsLocale(set, MinePlayerLocale)) {
                    set.add(MinePlayerLocale)
                } else {
                    plugin.sendLog(Level.WARNING, "Locale '&c$MinePlayerLocale&6' is duplicated!")
                }
            }
        }
        if (set.isEmpty()) throw LocaleException("Allowed locales is empty!")
        return set
    }

    @Throws(LocaleException::class)
    private fun parseDefaultLocale(locale: String): MineLocale {
        val loc = locale.replace(" ", "")
        if (loc.isEmpty()) throw LocaleException("Default locale is empty!")
        return MinePlayerMineLocale(loc)
    }

    private fun parseLocalesData(json: JSONObject): HashMap<UUID, MineLocale> {
        val map: HashMap<UUID, MineLocale> = HashMap<UUID, MineLocale>()
        json.keySet().forEach { map[UUID.fromString(it)] = MinePlayerMineLocale(json.getString(it)) }
        return map
    }

    private fun isContainsLocale(allowedMineLocales: Set<MineLocale>, mineLocale: MineLocale): Boolean {
        allowedMineLocales.forEach { if (it == mineLocale) return true }
        return false
    }
}