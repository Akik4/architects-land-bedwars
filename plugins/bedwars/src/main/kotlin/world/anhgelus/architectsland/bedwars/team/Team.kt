package world.anhgelus.architectsland.bedwars.team

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import world.anhgelus.architectsland.bedwars.utils.LocationHelper

enum class Team(
    val teamName: String,
    val color: ChatColor
) {
    RED("Red", ChatColor.RED),
    GREEN("Green", ChatColor.GREEN),
    BLUE("Blue", ChatColor.BLUE),
    YELLOW("Yellow", ChatColor.YELLOW),
    AQUA("Aqua", ChatColor.AQUA),
    GREY("Grey", ChatColor.GRAY),
    PINK("Pink", ChatColor.LIGHT_PURPLE),
    PURPLE("Pink", ChatColor.DARK_PURPLE);

    var hasBed: Boolean = true
        private set
    val players: List<Player> = listOf()
    var respawnLoc: Location? = null
        private set
    var generatorLoc: Location? = null
        private set

    var itemSellerLoc: Location? = null
        private set
    var upgradeSellerLoc: Location? = null
        private set

    fun setInConfig(section: ConfigurationSection) {
        section.set("color", this.color.toString())
        section.set("name", this.teamName)
        LocationHelper.setInConfig(this.respawnLoc!!, section.getConfigurationSection("location.respawn"))
        LocationHelper.setInConfig(this.generatorLoc!!, section.getConfigurationSection("location.generator"))
        LocationHelper.setInConfig(this.itemSellerLoc!!, section.getConfigurationSection("location.seller.item"))
        LocationHelper.setInConfig(this.upgradeSellerLoc!!, section.getConfigurationSection("location.seller.upgrade"))
    }

    private fun updateLocation(respawnLoc: Location, generatorLoc: Location, itemSellerLoc: Location, upgradeSellerLoc: Location) {
        this.respawnLoc = respawnLoc
        this.generatorLoc = generatorLoc
        this.itemSellerLoc = itemSellerLoc
        this.upgradeSellerLoc = upgradeSellerLoc
    }

    companion object {
        fun loadFromConfig(section: ConfigurationSection): Team? {
            val color = ChatColor.valueOf(section.getString("color")!!)
            val name = section.getString("name")
            // location
            val respawnLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.respawn"))
            val generatorLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.generator"))
            val itemSellerLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.seller.item"))
            val upgradeSellerLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.seller.upgrade"))

            entries.forEach { team ->
                if (team.color != color || team.name != name) {
                    return@forEach
                }
                team.updateLocation(respawnLoc, generatorLoc, itemSellerLoc, upgradeSellerLoc)
                return team
            }
            return null
        }
    }
}