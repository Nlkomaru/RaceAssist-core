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

package dev.nikomaru.raceassist.bet.commands

import cloud.commandframework.annotations.*
import dev.nikomaru.raceassist.data.files.BetSettingData
import dev.nikomaru.raceassist.utils.CommandUtils
import dev.nikomaru.raceassist.utils.Lang
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@CommandMethod("ra|RaceAssist bet")
class BetCanCommand {
    @CommandPermission("RaceAssist.commands.bet.can")
    @CommandMethod("can <raceId> <type>")
    suspend fun setCanBet(sender: CommandSender,
        @Argument(value = "raceId", suggestions = "raceId") raceId: String,
        @Argument(value = "type", suggestions = "betType") type: String) {
        if (CommandUtils.returnRaceSetting(raceId, sender)) return
        if (type == "on") {
            setCanBet(raceId, sender)
        } else if (type == "off") {
            setCannotBet(raceId, sender)
        }

    }

    private suspend fun setCanBet(raceId: String, sender: CommandSender) {
        BetSettingData.setAvailable(raceId, true)
        val locale = if (sender is Player) sender.locale() else Locale.getDefault()
        sender.sendMessage(Lang.getComponent("can-bet-this-raceid", locale, raceId))
    }

    companion object {
        suspend fun setCannotBet(raceId: String, sender: CommandSender) {
            BetSettingData.setAvailable(raceId, false)

            val locale = if (sender is Player) sender.locale() else Locale.getDefault()
            sender.sendMessage(Lang.getComponent("cannot-bet-this-raceid", locale, raceId))
        }
    }
}