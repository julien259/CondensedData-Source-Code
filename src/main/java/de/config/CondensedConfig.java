package de.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CondensedConfig {
    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("condenseddata.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int throttleDistance = 48;
    public int throttleInterval = 10;
    public boolean autoTune = true;

    public static CondensedConfig load() {
        CondensedConfig config = new CondensedConfig();
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                config = GSON.fromJson(reader, CondensedConfig.class);
            } catch (Exception ignored) {}
        }

        if (config.autoTune) {
            config.performAutoTune();
        }
        return config;
    }
    private void performAutoTune() {
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores <= 4) {
            this.throttleDistance = 32;
            this.throttleInterval = 5;
        } else if (cores <= 8) {
            this.throttleDistance = 48;
            this.throttleInterval = 10;
        } else {
            this.throttleDistance = 64;
            this.throttleInterval = 20;
        }
    }
    public void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException ignored) {}
    }
}