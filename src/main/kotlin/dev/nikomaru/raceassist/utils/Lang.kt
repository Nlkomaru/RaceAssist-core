/*
 *     Copyright © 2021-2022 Nikomaru <nikomaru@nikomaru.dev>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nikomaru.raceassist.utils

import dev.nikomaru.raceassist.RaceAssist.Companion.plugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.io.*
import java.nio.file.Files
import java.text.MessageFormat
import java.util.*

object Lang {
    private val langList: HashMap<String, Properties> = HashMap()

    val mm = MiniMessage.miniMessage()

    suspend fun load() {
        withContext(Dispatchers.IO) {
            val lang = listOf("ja_JP", "de_DE", "en_US", "fr_FR", "ko_KR", "zh_CN", "zh_TW")

            lang.forEach { locale ->
                val conf = Properties()
                conf.load(InputStreamReader(this.javaClass.classLoader.getResourceAsStream("lang/$locale.properties")!!, "UTF-8"))
                langList[locale] = conf
            }

            if (!plugin.dataFolder.exists()) {
                plugin.dataFolder.mkdir()
            }
            val langDir = File(plugin.dataFolder, "lang")
            if (!langDir.exists()) {
                langDir.mkdir()
            }
            lang.forEach { locale ->
                val input: InputStream = this.javaClass.classLoader.getResourceAsStream("lang/$locale.properties") ?: return@forEach
                plugin.logger.info("Loading resource lang file for $locale")
                val file = File(langDir, "$locale.properties")
                if (!file.exists()) {
                    Files.copy(input, file.toPath())
                }
            }
            withContext(Dispatchers.IO) {
                langDir.listFiles()?.forEach {
                    plugin.logger.info("Loading local lang file for ${it.nameWithoutExtension}")
                    langList[it.nameWithoutExtension] = Properties().apply {
                        load(InputStreamReader(it.inputStream(), "UTF-8"))
                    }
                }
            }
        }

    }

    fun getComponent(key: String, locale: Locale, vararg args: Any?): Component {
        val lang = langList[locale.toString()] ?: langList["ja_JP"]
        return lang?.getProperty(key)?.let { mm.deserialize(MessageFormat.format(it, *args)) } ?: mm.deserialize(key)
    }

    fun getText(key: String, locale: Locale, vararg args: Any?): String {
        val lang = langList[locale.toString()] ?: langList["ja_JP"]
        return lang?.getProperty(key)?.let { MessageFormat.format(it, *args) } ?: key
    }

}
