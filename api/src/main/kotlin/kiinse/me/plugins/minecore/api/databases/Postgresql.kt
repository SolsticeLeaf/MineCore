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
package kiinse.me.plugins.minecore.api.databases

import kiinse.me.plugins.minecore.api.MineCorePlugin
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.logging.Level

@Suppress("unused")
abstract class Postgresql protected constructor(plugin: MineCorePlugin) {
    private val plugin: MineCorePlugin
    var connection: Connection? = null
    var context: DSLContext? = null
    val isConnected: Boolean
        get() = connection != null

    init {
        connect()
        this.plugin = plugin
    }

    @Throws(SQLException::class)
    fun connect() {
        if (!isConnected) {
            try {
                plugin.sendLog("Connecting to database...")
                System.getProperties().setProperty("org.jooq.no-logo", "true")
                System.getProperties().setProperty("org.jooq.no-tips", "true")
                connection = registerConnection(getSettings(plugin))
                context = DSL.using(connection, SQLDialect.POSTGRES)
                createDataBases(context)
                plugin.sendLog("Database connected.")
            } catch (e: Exception) {
                throw SQLException(e)
            }
        } else {
            throw SQLException("Database already connected!")
        }
    }

    abstract fun getSettings(plugin: MineCorePlugin): SQLConnectionSettings

    @Throws(Exception::class)
    abstract fun registerConnection(settings: SQLConnectionSettings): Connection

    abstract fun createDataBases(context: DSLContext?)

    fun getURL(settings: SQLConnectionSettings): String {
        val url = "jdbc:" + settings.urlDriver + "://" + settings.host + ":" + settings.port + "/"
        plugin.sendLog(Level.CONFIG, "Database connection url: &d$url")
        return url
    }

    fun getProperties(settings: SQLConnectionSettings): Properties {
        val connInfo = Properties()
        connInfo.setProperty("user", settings.login)
        connInfo.setProperty("password", settings.password)
        connInfo.setProperty("useUnicode", "true")
        connInfo.setProperty("characterEncoding", "UTF-8")
        plugin.sendLog(Level.CONFIG, "Database user: &d" + settings.login)
        plugin.sendLog(Level.CONFIG, "Database password: &d****" + settings.login.substring(4))
        return connInfo
    }
}