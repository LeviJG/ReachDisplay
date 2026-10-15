package net.blueskiez77.reach_display.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.blueskiez77.reach_display.ReachDisplay;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "reach_display.json";

    public static void load() {
        DisplayConfig displayConfig = DisplayConfig.INSTANCE;
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (!Files.exists(path)) return;
        try (Reader reader = Files.newBufferedReader(path)) {
            DisplayConfig loaded = GSON.fromJson(reader, DisplayConfig.class);
            if (loaded == null) return;

            // Copy every setting, so new fields in DisplayConfig are loaded without being listed here.
            // Settings missing from the file keep their defaults (Gson starts from new DisplayConfig()), and null
            // ones (e.g. an enum value that no longer exists) are skipped so they keep theirs too.
            for (Field field : DisplayConfig.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                Object value = field.get(loaded);
                if (value != null) field.set(displayConfig, value);
            }
            resetOffScreenPositions(displayConfig);
        } catch (IOException | JsonParseException e) {
            ReachDisplay.LOGGER.error("Couldn't read {}, using the default settings", FILE_NAME, e);
        } catch (IllegalAccessException e) {
            ReachDisplay.LOGGER.error("Couldn't apply the settings from {}", FILE_NAME, e);
        }
    }

    // Positions are fractions of the screen (0 to 1). Configs from older versions stored pixel offsets
    // (e.g. hitYOffset = -30), which put the number off-screen where the layout editor can't grab it.
    private static void resetOffScreenPositions(DisplayConfig c) {
        DisplayConfig defaults = new DisplayConfig();
        if (isOffScreen(c.distanceXOffset, c.distanceYOffset)) {
            c.distanceXOffset = defaults.distanceXOffset;
            c.distanceYOffset = defaults.distanceYOffset;
        }
        if (isOffScreen(c.hitXOffset, c.hitYOffset)) {
            c.hitXOffset = defaults.hitXOffset;
            c.hitYOffset = defaults.hitYOffset;
        }
        if (isOffScreen(c.averageHitXOffset, c.averageHitYOffset)) {
            c.averageHitXOffset = defaults.averageHitXOffset;
            c.averageHitYOffset = defaults.averageHitYOffset;
        }
    }

    private static boolean isOffScreen(double x, double y) {
        return !(x >= 0.0 && x <= 1.0 && y >= 0.0 && y <= 1.0);
    }

    public static void save() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(DisplayConfig.INSTANCE, writer);
        } catch (IOException e) {
            ReachDisplay.LOGGER.error("Couldn't save {}", FILE_NAME, e);
        }
    }
}