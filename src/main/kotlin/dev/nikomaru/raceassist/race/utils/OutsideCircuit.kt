/*
 * Copyright © 2022 Nikomaru <nikomaru@nikomaru.dev>
 * This program is free software: you can redistribute it and/or modify
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
package dev.nikomaru.raceassist.race.utils

import dev.nikomaru.raceassist.RaceAssist.Companion.plugin
import dev.nikomaru.raceassist.database.CircuitPoint
import dev.nikomaru.raceassist.race.commands.CommandUtils.canSetOutsideCircuit
import dev.nikomaru.raceassist.race.commands.CommandUtils.circuitRaceID

import dev.nikomaru.raceassist.utils.Lang
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.awt.Polygon
import java.text.MessageFormat

object OutsideCircuit {
    private var outsidePolygonMap = HashMap<String, Polygon>()
    private var insidePolygonMap = HashMap<String, Polygon>()
    suspend fun outsideCircuit(player: Player, raceId: String, x: Int, z: Int) {
        outsidePolygonMap.putIfAbsent(raceId, Polygon())
        insidePolygonMap.putIfAbsent(raceId, Polygon())
        if (insidePolygonMap[raceId]!!.npoints == 0) {
            newSuspendedTransaction(Dispatchers.IO) {
                CircuitPoint.select { (CircuitPoint.raceID eq raceId) and (CircuitPoint.inside eq true) }.forEach {
                    insidePolygonMap[raceId]!!.addPoint(it[CircuitPoint.XPoint], it[CircuitPoint.YPoint])
                }
            }
        }

        if (insidePolygonMap[raceId]!!.contains(x, z)) {
            player.sendActionBar(text(Lang.getText("to-click-inside-point", player.locale())))
            return
        }
        outsidePolygonMap[raceId]!!.addPoint(x, z)
        player.sendActionBar(text(MessageFormat.format(Lang.getText("to-click-next-point", player.locale()), x, z)))
        canSetOutsideCircuit.remove(player.uniqueId)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            canSetOutsideCircuit[player.uniqueId] = true
        }, 5)
    }

    suspend fun finish(player: Player) {

        newSuspendedTransaction(Dispatchers.IO) {
            CircuitPoint.deleteWhere {
                (CircuitPoint.raceID eq circuitRaceID[player.uniqueId]!!) and (CircuitPoint.inside eq
                        false)
            }
        }
        val x = outsidePolygonMap[circuitRaceID[player.uniqueId]]!!.xpoints
        val y = outsidePolygonMap[circuitRaceID[player.uniqueId]]!!.ypoints
        val n = outsidePolygonMap[circuitRaceID[player.uniqueId]]!!.npoints
        for (i in 0 until n) {
            newSuspendedTransaction(Dispatchers.IO) {
                CircuitPoint.insert {
                    it[raceID] = circuitRaceID[player.uniqueId]!!
                    it[inside] = false
                    it[XPoint] = x[i]
                    it[YPoint] = y[i]
                }
            }
        }
        outsidePolygonMap.remove(circuitRaceID[player.uniqueId])
        player.sendMessage(text("設定完了しました", TextColor.color(GREEN)))
    }
}