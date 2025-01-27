package dev.thomasqtruong.veryscuffedcobblemonbreeding.neoforge;

import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;



public class ForgeEventHandler {
    public void register() {
        NeoForge.EVENT_BUS.register(this);
        VeryScuffedCobblemonBreeding.INSTANCE.getLogger().info("Registered VeryScuffedCobblemonBreeding Forge Event Handler");
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    }
}