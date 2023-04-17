package dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig;
import net.minecraft.command.CommandSource;

public class VeryScuffedCobblemonBreedingPermissions {

    public final CobblemonPermission POKEBREED_PERMISSION;
    public final CobblemonPermission VIP_POKEBREED_PERMISSION;

    public VeryScuffedCobblemonBreedingPermissions() {
        this.POKEBREED_PERMISSION = new CobblemonPermission("veryscuffedcobblemonbreeding.command.pokebreed", toPermLevel(VeryScuffedCobblemonBreedingConfig.COMMAND_POKEBREED_PERMISSION_LEVEL));
        this.VIP_POKEBREED_PERMISSION = new CobblemonPermission("veryscuffedcobblemonbreeding.command.vippokebreed", toPermLevel(VeryScuffedCobblemonBreedingConfig.VIP_COMMAND_POKEBREED_PERMISSION_LEVEL));
    }

    public PermissionLevel toPermLevel(int permLevel) {
        for (PermissionLevel value : PermissionLevel.values()) {
            if (value.ordinal() == permLevel) {
                return value;
            }
        }
        return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS;
    }

    public static boolean checkPermission(CommandSource source, CobblemonPermission permission) {
        return Cobblemon.INSTANCE.getPermissionValidator().hasPermission(source, permission);
    }
}
