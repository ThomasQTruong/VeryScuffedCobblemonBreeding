package dev.thomasqtruong.veryscuffedcobblemonbreeding.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

public class VeryScuffedCobblemonBreedingConfig {
    Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static int COMMAND_POKEBREED_PERMISSION_LEVEL = 2;
    public static int COOLDOWN_IN_MINUTES = 5;  // Default: 5 minutes cooldown.

    public VeryScuffedCobblemonBreedingConfig() {
        init();
    }

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
            COOLDOWN_IN_MINUTES = permissionMap.getOrDefault("command.pokebreed.cooldown", 5);

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
                    .endObject()
                    .beginObject()
                        .name("command.pokebreed.cooldown")
                        .value(COOLDOWN_IN_MINUTES)
                    .endObject()
                .endObject()
                .flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
