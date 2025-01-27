package dev.thomasqtruong.veryscuffedcobblemonbreeding.neoforge;

import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(VeryScuffedCobblemonBreeding.MODID)

public final class VeryScuffedCobblemonBreedingForge {
    final ForgeEventHandler forgeEventHandler = new ForgeEventHandler();

    public VeryScuffedCobblemonBreedingForge() {
        VeryScuffedCobblemonBreeding.INSTANCE.getLogger().info("VeryScuffedCobblemonBreeding NeoForge Starting...");
        VeryScuffedCobblemonBreeding.INSTANCE.initialize();
        NeoForge.EVENT_BUS.register(VeryScuffedCobblemonBreedingForge.class);
        forgeEventHandler.register();
    }

    @SubscribeEvent
    public static void onCommandRegistration(final RegisterCommandsEvent event) {
        VeryScuffedCobblemonBreeding.INSTANCE.registerCommands(event.getDispatcher());
    }
}