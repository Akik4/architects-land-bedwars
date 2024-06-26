package world.anhgelus.architectsland.bedwars.utils.config

import world.anhgelus.architectsland.bedwars.Bedwars

object ConfigAPI {
    private var main: Bedwars? = null

    val teamConfigFile = "teams"

    fun config(name: String): Config {
        return Config(main!!, name)
    }

    fun setup(main: Bedwars) {
        ConfigAPI.main = main
    }
}