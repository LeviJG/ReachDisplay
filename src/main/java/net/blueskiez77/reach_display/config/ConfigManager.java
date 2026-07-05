package net.blueskiez77.reach_display.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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
            if (loaded != null) {

                // -------------------- Load Internal Vars -------------------- //
                displayConfig.distanceXOffset = loaded.distanceXOffset;
                displayConfig.distanceYOffset = loaded.distanceYOffset;
                displayConfig.hitXOffset = loaded.hitXOffset;
                displayConfig.hitYOffset = loaded.hitYOffset;
                displayConfig.averageHitXOffset = loaded.averageHitXOffset;
                displayConfig.averageHitYOffset = loaded.averageHitYOffset;

                // -------------------- Load General Vars -------------------- //
                displayConfig.enabled = loaded.enabled;
                displayConfig.showPlayersOnly = loaded.showPlayersOnly;
                displayConfig.distanceCalculationMethod = DisplayConfig.DistanceCalculationMethod.CLOSEST_POINT;

                // -------------------- Load Distance Vars -------------------- //
                displayConfig.distanceEnabled = loaded.distanceEnabled;
                displayConfig.distanceTextScale = loaded.distanceTextScale;
                displayConfig.distanceTextColor = loaded.distanceTextColor;
                displayConfig.distanceTextOpacity = loaded.distanceTextOpacity;
                displayConfig.distanceTextShadow = loaded.distanceTextShadow;
                displayConfig.distanceDecimalPlaces = loaded.distanceDecimalPlaces;

                // -------------------- Load Hit Distance Vars -------------------- //
                displayConfig.hitDistanceEnabled = loaded.hitDistanceEnabled;
                displayConfig.hitDistanceTextScale = loaded.hitDistanceTextScale;
                displayConfig.hitDistanceTextColor = loaded.hitDistanceTextColor;
                displayConfig.hitDistanceTextOpacity = loaded.hitDistanceTextOpacity;
                displayConfig.hitDistanceTextShadow = loaded.hitDistanceTextShadow;
                displayConfig.hitDistanceDecimalPlaces = loaded.hitDistanceDecimalPlaces;

                // -------------------- Load Average Hit Distance Vars -------------------- //
                displayConfig.averageHitEnabled = loaded.averageHitEnabled;
                displayConfig.numberOfHitsCounted = loaded.numberOfHitsCounted;
                displayConfig.averageHitTextScale = loaded.averageHitTextScale;
                displayConfig.averageHitTextColor = loaded.averageHitTextColor;
                displayConfig.averageHitTextOpacity = loaded.averageHitTextOpacity;
                displayConfig.averageHitTextShadow = loaded.averageHitTextShadow;
                displayConfig.averageHitDecimalPlaces = loaded.averageHitDecimalPlaces;
            }
        } catch (IOException e) {
            // log
        }
    }

    public static void save() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(DisplayConfig.INSTANCE, writer);
        } catch (IOException e) {
            // log
        }
    }
}