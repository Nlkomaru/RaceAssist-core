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

package dev.nikomaru.raceassist.race.commands.player

import cloud.commandframework.annotations.*
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.nikomaru.raceassist.RaceAssist.Companion.plugin
import dev.nikomaru.raceassist.data.files.RaceData
import dev.nikomaru.raceassist.utils.CommandUtils
import org.bukkit.command.CommandSender

@CommandMethod("ra|RaceAssist player")
class PlayerListCommand {

    @CommandPermission("RaceAssist.commands.player.list")
    @CommandMethod("list <raceId>")
    fun displayPlayerList(sender: CommandSender, @Argument(value = "raceId", suggestions = "raceId") raceId: String) {
        plugin.launch {
            if (CommandUtils.returnRaceSetting(raceId, sender)) return@launch
            if (RaceData.getJockeys(raceId).isEmpty()) {
                sender.sendMessage("<color:red>プレイヤーはいません")
            }

            RaceData.getJockeys(raceId).forEach {
                sender.sendMessage(it.name.toString())
            }

        }
    }
}