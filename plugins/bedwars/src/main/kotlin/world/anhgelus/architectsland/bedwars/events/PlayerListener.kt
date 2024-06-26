package world.anhgelus.architectsland.bedwars.events

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import world.anhgelus.architectsland.bedwars.Bedwars
import world.anhgelus.architectsland.bedwars.team.Team
import world.anhgelus.architectsland.bedwars.team.TeamPlayer

object PlayerListener : Listener {

    val breakable = listOf(
        Material.WOOL, Material.BED, Material.ENDER_STONE, Material.OBSIDIAN, Material.CLAY, Material.GLASS, Material.WOOD /* All planks*/
    )

    @EventHandler
    fun onPlayerDeath(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        if (player.health - event.damage > 0) {
            return
        }
        event.isCancelled = true
        val tp = TeamPlayer.fromPlayer(player)!!
        if (event is EntityDamageByEntityEvent && event.damager is Player) {
            tp.kill(event.damager as Player)
        } else {
            tp.die()
        }

        if (tp.team.canRespawn()) {
            Bukkit.getScheduler().runTaskLater(Bedwars.instance, {
                tp.respawn()
            }, 5*20L)
            return
        }

        val survivals = Bukkit.getOnlinePlayers().filter {
            it.gameMode == GameMode.SURVIVAL
        }
        val teams = mutableSetOf<Team>()
        survivals.forEach {
            teams.add(TeamPlayer.fromPlayer(it)!!.team)
        }
        if (teams.size > 1) {
            return
        }

        player.world.players.forEach {
            it.inventory.clear();
            if(teams.first().players.contains(it)){
                it.allowFlight = true;
                @Suppress("DEPRECATION")
                it.sendTitle("Victoire", "--------------------")
            } else {
                it.gameMode = GameMode.SPECTATOR;
                @Suppress("DEPRECATION")
                it.sendTitle("Défaite", "Vitoire de l'équipe ${teams.first().teamName} !")
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.block.type !in breakable) {
            event.isCancelled = true
            return
        }
        if (event.block.type != Material.BED) {
            return
        }
        val team = Team.getFromBedLocation(event.block.location) ?:
            throw NullPointerException("team from location ${event.block.location} is null")
        team.lostBed()
    }
}