package com.lootvault.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(), "lootvault.json");

    private static ModConfig INSTANCE = new ModConfig();

    public boolean enabled = true;
    public int delayMs = 0;
    public boolean autoClose = true;
    public int autoCloseDelayMs = 0;
    public boolean chatNotifications = true;

    public boolean lootShulkers = true;
    public boolean lootChests = true;
    public boolean lootBarrels = true;

    public boolean smartFilter = false;
    public String lootMode = "all";
    public boolean randomOrder = true;

    public int keyCode = 74;
    public String keyName = "J";

    public String language = "en";

    public static ModConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static ModConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    INSTANCE = config;
                    return INSTANCE;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        INSTANCE = new ModConfig();
        INSTANCE.save();
        return INSTANCE;
    }

    public void save() {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
