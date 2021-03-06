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
import dev.nikomaru.raceassist.data.files.RaceSettingData
import dev.nikomaru.raceassist.utils.CommandUtils
import dev.nikomaru.raceassist.utils.Lang
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@CommandMethod("ra|RaceAssist player")
class PlayerAddCommand {

    @CommandPermission("RaceAssist.commands.player.add")
    @CommandMethod("add <raceId> <playerName>")
    suspend fun addPlayer(sender: CommandSender,
        @Argument(value = "raceId", suggestions = "raceId") raceId: String,
        @Argument(value = "playerName", suggestions = "playerName") playerName: String) {
        if (CommandUtils.returnRaceSetting(raceId, sender)) return

        val locale = if (sender is Player) sender.locale() else Locale.getDefault()

        val jockey: OfflinePlayer =
            Bukkit.getOfflinePlayerIfCached(playerName) ?: return sender.sendMessage(Lang.getComponent("player-add-not-exist", locale))


        if (RaceSettingData.getJockeys(raceId).contains(jockey)) {
            sender.sendMessage(Lang.getComponent("already-exist-this-user", locale))
            return
        }
        if (RaceSettingData.getJockeys(raceId).size > 7) {
            sender.sendMessage(Lang.getComponent("max-player-is-eight", locale))
            return
        }
        RaceSettingData.addJockey(raceId, jockey)
        sender.sendMessage(Lang.getComponent("player-add-to-race-group", locale, jockey.name.toString(), raceId))

    }
}