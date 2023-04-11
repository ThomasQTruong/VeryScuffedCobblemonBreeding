package dev.thomasqtruong.veryscuffedcobblemonbreeding.fabric

import com.cobblemon.mod.common.api.permission.CobblemonPermission
import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding
import net.fabricmc.api.ModInitializer

class CobblemonFabric : ModInitializer {
    override fun onInitialize() {
        System.out.println("Fabric Mod init")
        VeryScuffedCobblemonBreeding.initialize();
    }
}
