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
package kiinse.me.plugins.minecore.lib.files.messages

import kiinse.me.plugins.minecore.api.MineCorePlugin
import kiinse.me.plugins.minecore.api.exceptions.JsonFileException
import kiinse.me.plugins.minecore.api.files.enums.Directory
import kiinse.me.plugins.minecore.api.files.filemanager.FilesManager
import kiinse.me.plugins.minecore.api.files.locale.MineLocale
import kiinse.me.plugins.minecore.api.files.messages.Message
import kiinse.me.plugins.minecore.api.files.messages.Messages
import kiinse.me.plugins.minecore.api.files.messages.MessagesKeys
import kiinse.me.plugins.minecore.lib.utilities.MineUtils
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.logging.Level
import kotlin.collections.HashMap

class MineMessages(plugin: MineCorePlugin) : FilesManager(plugin), Messages {
    private val plugin: MineCorePlugin
    override val allMessages = HashMap<String, JSONObject>()

    init {
        this.plugin = plugin
        val directoryName = Directory.MESSAGES
        if (isFileNotExists(directoryName) || isDirectoryEmpty(directoryName)) copyFile(directoryName)
        load()
    }

    @Throws(JsonFileException::class)
    override fun reload() {
        load()
    }

    @Throws(JsonFileException::class)
    private fun load() {
        val mineCore = plugin.mineCore
        val locales = mineCore.localeStorage.allowedLocalesString
        listFilesInDirectory(Directory.MESSAGES).forEach {
            val locale = it.name.split(".")[0]
            if (!locales.contains(locale)) {
                plugin.sendLog(Level.WARNING,
                               "Found localization file for language '&c$locale&6' which is not loaded in &bMineCore&6. This file will &cnot&6 be loaded into memory.")
            } else {
                allMessages[locale] = getJsonFromFile(it)
            }
        }
        mineCore.localeStorage.allowedLocalesList.forEach {
            if (!isContainsLocale(it))
                plugin.sendLog(Level.WARNING, "&cNo localization file found &6for language '&c$it&6', which is available in &bMineCore&6.")
        }
        allMessages.forEach { (locale, value) ->
            value.keySet().forEach { entryKey ->
                allMessages.forEach { (key, value1) ->
                    if (key != locale && !value1.keySet().contains(entryKey))
                        plugin.sendLog(Level.WARNING, "Key '&c$entryKey&6' was not found in localization '&с$key&6', which was found in localization '&с$locale&6'!")
                }
            }
        }
    }

    private fun isContainsLocale(mineLocale: MineLocale): Boolean {
        allMessages.keys.forEach { if (mineLocale.toString() == it) return true }
        return false
    }

    override fun getStringMessage(mineLocale: MineLocale, message: MessagesKeys): String {
        return MineUtils.colorize(getString(mineLocale, message))
    }

    override fun getStringMessageWithPrefix(mineLocale: MineLocale, message: MessagesKeys): String {
        return getPrefix(mineLocale) + MineUtils.colorize(getString(mineLocale, message))
    }

    override fun getAllLocaleMessages(mineLocale: MineLocale): JSONObject {
        return allMessages[mineLocale.toString()]!!
    }

    override fun getPrefix(mineLocale: MineLocale): String {
        return MineUtils.colorize(getString(mineLocale, Message.PREFIX))
    }

    private fun getString(mineLocale: MineLocale, message: MessagesKeys): String {
        val json = getAllLocaleMessages(mineLocale)
        val key = message.toString().lowercase(Locale.getDefault())
        return if (json.has(key)) json.getString(key) else key
    }

    @Throws(JsonFileException::class)
    private fun getJsonFromFile(file: File): JSONObject {
        try {
            BufferedReader(FileReader(file.absolutePath)).use {
                val json = JSONObject(Files.readString(Paths.get(file.absolutePath)))
                plugin.sendLog("Messages '&b${file.name}&a' loaded")
                return json
            }
        } catch (e: IOException) {
            throw JsonFileException(e)
        }
    }
}