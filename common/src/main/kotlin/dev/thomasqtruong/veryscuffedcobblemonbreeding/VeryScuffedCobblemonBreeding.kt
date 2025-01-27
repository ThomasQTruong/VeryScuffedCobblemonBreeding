package dev.thomasqtruong.veryscuffedcobblemonbreeding

import com.mojang.brigadier.CommandDispatcher
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.*
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.CobblemonConfig
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig
import dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions.VeryScuffedCobblemonBreedingPermissions
import net.minecraft.commands.CommandSourceStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object VeryScuffedCobblemonBreeding {
  public lateinit var permissions: VeryScuffedCobblemonBreedingPermissions
  const val MODID = "veryscuffedcobblemonbreeding"
  var LOGGER: Logger = LogManager.getLogger("[VeryScuffedCobblemonBreeding]")

  fun initialize() {
    getLogger().info("VeryScuffedCobblemonBreedingPermissions - Initialized")
    VeryScuffedCobblemonBreedingConfig() // must load before permissions so perms use default permission level.
    this.permissions = VeryScuffedCobblemonBreedingPermissions()

    // Load official Cobblemon's config.
    CobblemonConfig()
  }

  fun getLogger(): Logger {
    return this.LOGGER;
  }

  fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
    PokeBreed().register(dispatcher)
  }
}
