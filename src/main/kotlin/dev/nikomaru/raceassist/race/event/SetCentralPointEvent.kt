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

package dev.nikomaru.raceassist.race.event

import dev.nikomaru.raceassist.data.files.PlaceSettingData
import dev.nikomaru.raceassist.utils.CommandUtils.canSetCentral
import dev.nikomaru.raceassist.utils.CommandUtils.centralRaceId
import dev.nikomaru.raceassist.utils.Lang
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class SetCentralPointEvent : Listener {
    @EventHandler
    suspend fun setCentralPoint(event: PlayerInteractEvent) {
        if (canSetCentral[event.player.uniqueId] != true) {
            return
        }
        if (event.action != Action.LEFT_CLICK_BLOCK) {
            return
        }

        PlaceSettingData.setCentralXPoint(centralRaceId[event.player.uniqueId]!!, event.clickedBlock?.location?.blockX ?: 0)
        PlaceSettingData.setCentralYPoint(centralRaceId[event.player.uniqueId]!!, event.clickedBlock?.location?.blockZ ?: 0)

        event.player.sendMessage(Lang.getComponent("to-set-this-point-central", event.player.locale()))
        canSetCentral.remove(event.player.uniqueId)
    }
}