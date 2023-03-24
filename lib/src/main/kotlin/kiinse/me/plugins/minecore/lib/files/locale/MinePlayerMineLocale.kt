package kiinse.me.plugins.minecore.lib.files.locale

import kiinse.me.plugins.minecore.api.files.locale.MineLocale

class MinePlayerMineLocale(private val value: String) : MineLocale() {

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is MineLocale && this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}