package dev.thomasqtruong.veryscuffedcobblemonbreeding

import com.mojang.brigadier.CommandDispatcher
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.*
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.CobblemonConfig
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig
import dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions.VeryScuffedCobblemonBreedingPermissions
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object VeryScuffedCobblemonBreeding {
  public lateinit var permissions: VeryScuffedCobblemonBreedingPermissions
  const val MODID = "veryscuffedcobblemonbreeding"
  fun initialize() {
    System.out.println("VeryScuffedCobblemonBreedingPermissions - Initialized")
    VeryScuffedCobblemonBreedingConfig() // must load before permissions so perms use default permission level.
    this.permissions = VeryScuffedCobblemonBreedingPermissions()

    // Load official Cobblemon's config.
    CobblemonConfig()

    CommandRegistrationEvent.EVENT.register(VeryScuffedCobblemonBreeding::registerCommands)
  }

  fun registerCommands(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    registry: CommandRegistryAccess,
    selection: CommandManager.RegistrationEnvironment
  ) {
    PokeBreed().register(dispatcher)
  }
}
