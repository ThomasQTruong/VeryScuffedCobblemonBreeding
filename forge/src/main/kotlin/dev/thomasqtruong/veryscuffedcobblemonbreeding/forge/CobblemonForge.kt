package dev.thomasqtruong.veryscuffedcobblemonbreeding.forge

import dev.architectury.platform.forge.EventBuses
import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding
import java.util.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent

@Mod(VeryScuffedCobblemonBreeding.MODID)
class VeryScuffedCobblemonBreeding {
    init {
        with(thedarkcolour.kotlinforforge.forge.MOD_BUS) {
            EventBuses.registerModEventBus(VeryScuffedCobblemonBreeding.MODID, this)
            addListener(this@VeryScuffedCobblemonBreeding::initialize)
            addListener(this@VeryScuffedCobblemonBreeding::serverInit)
        }
    }

    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        VeryScuffedCobblemonBreeding.initialize()
        System.out.println("VeryScuffedCobblemonBreeding Forge Init.")
    }

}