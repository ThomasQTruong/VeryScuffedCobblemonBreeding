package dev.thomasqtruong.veryscuffedcobblemonbreeding.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class VeryScuffedCobblemonBreedingConfig {
    Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static int COMMAND_POKEBREED_PERMISSION_LEVEL = 2;      // Default for MC is 2.
    public static int VIP_COMMAND_POKEBREED_PERMISSION_LEVEL = 3;  // VIP permission level.
    public static int COOLDOWN_IN_MINUTES = 5;      // Default: 5 minutes cooldown.
    public static int VIP_COOLDOWN_IN_MINUTES = 3;  // VIP breeding cooldown, default: 3.

    public VeryScuffedCobblemonBreedingConfig() {
        init();
    }

    // Extracts data from the config file.
    public void init() {
        File configFolder = new File(System.getProperty("user.dir") + "/config/veryscuffedcobblemonbreeding");
        File configFile = new File(configFolder, "config.json");
        System.out.println("VeryScuffedCobblemonBreeding config -> " + configFolder.getAbsolutePath());
        if (!configFolder.exists()) {
            configFolder.mkdirs();
            createConfig(configFolder);
        } else if (!configFile.exists()) {
            createConfig(configFolder);
        }

        try {
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            JsonObject obj = GSON.fromJson(new FileReader(configFile), JsonObject.class);

            JsonObject permLevels = obj.get("permissionlevels").getAsJsonObject();
            HashMap<String, Integer> permissionMap = GSON.fromJson(permLevels, type);
            COMMAND_POKEBREED_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pokebreed", 2);
            VIP_COMMAND_POKEBREED_PERMISSION_LEVEL = permissionMap.getOrDefault("command.vippokebreed", 3);

            JsonObject cooldowns = obj.get("cooldowns").getAsJsonObject();
            HashMap<String, Integer> cooldownsMap = GSON.fromJson(cooldowns, type);
            COOLDOWN_IN_MINUTES = cooldownsMap.getOrDefault("command.pokebreed.cooldown", 5);
            VIP_COOLDOWN_IN_MINUTES = cooldownsMap.getOrDefault("command.pokebreed.vipcooldown", 3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createConfig(File configFolder) {
        File file = new File(configFolder, "config.json");
        try {
            file.createNewFile();
            JsonWriter writer = GSON.newJsonWriter(new FileWriter(file));
            writer.beginObject()
                    .name("permissionlevels")
                        .beginObject()
                            .name("command.pokebreed")
                            .value(COMMAND_POKEBREED_PERMISSION_LEVEL)
                            .name("command.vippokebreed")
                            .value(VIP_COMMAND_POKEBREED_PERMISSION_LEVEL)
                        .endObject()
                    .name("cooldowns")
                        .beginObject()
                            .name("command.pokebreed.cooldown")
                            .value(COOLDOWN_IN_MINUTES)
                            .name("command.pokebreed.vipcooldown")
                            .value(VIP_COOLDOWN_IN_MINUTES)
                        .endObject()
                    .endObject()
                    .flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
