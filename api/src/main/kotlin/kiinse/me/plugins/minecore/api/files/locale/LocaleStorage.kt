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
package kiinse.me.plugins.minecore.api.files.locale

import kiinse.me.plugins.minecore.api.exceptions.JsonFileException
import kiinse.me.plugins.minecore.api.exceptions.LocaleException
import org.bukkit.entity.Player
import java.util.*

@Suppress("unused")
interface LocaleStorage {
    @Throws(LocaleException::class, JsonFileException::class) fun load(): LocaleStorage
    @Throws(JsonFileException::class) fun save(): LocaleStorage
    fun isAllowedLocale(mineLocale: MineLocale): Boolean
    fun putInLocalesData(uuid: UUID, mineLocale: MineLocale): Boolean
    fun putInLocalesData(player: Player, mineLocale: MineLocale): Boolean
    fun isLocalesDataContains(uuid: UUID): Boolean
    fun isLocalesDataContains(player: Player): Boolean
    fun getLocalesData(uuid: UUID): MineLocale?
    fun getLocalesData(player: Player): MineLocale?
    fun removeLocalesData(uuid: UUID): Boolean
    fun removeLocalesData(player: Player): Boolean
    val defaultMineLocale: MineLocale
    val localesData: HashMap<UUID, MineLocale>
    val allowedLocalesString: String
    val allowedLocalesList: Set<MineLocale>
    val allowedLocalesListString: Set<String>
}