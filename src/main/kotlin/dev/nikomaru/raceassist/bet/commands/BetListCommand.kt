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
import dev.nikomaru.raceassist.data.database.BetList
import dev.nikomaru.raceassist.utils.CommandUtils
import dev.nikomaru.raceassist.utils.Lang
import kotlinx.coroutines.Dispatchers
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

@CommandMethod("ra|RaceAssist bet")
class BetListCommand {
    @CommandPermission("RaceAssist.commands.bet.list")
    @CommandMethod("list <raceId>")
    suspend fun list(sender: CommandSender, @Argument(value = "raceId", suggestions = "raceId") raceId: String) {

        val locale = if (sender is Player) sender.locale() else Locale.getDefault()

        if (CommandUtils.returnRaceSetting(raceId, sender)) return
        newSuspendedTransaction(Dispatchers.IO) {
            BetList.select { BetList.raceId eq raceId }.forEach {
                sender.sendMessage(Lang.getComponent("bet-list-detail-message",
                    locale,
                    it[BetList.rowNum],
                    it[BetList.timeStamp],
                    it[BetList.playerName],
                    it[BetList.jockey],
                    it[BetList.betting]))
            }
        }

    }

}