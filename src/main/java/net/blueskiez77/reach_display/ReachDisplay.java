package net.blueskiez77.reach_display;

import net.blueskiez77.reach_display.config.ConfigManager;
import net.blueskiez77.reach_display.config.Hotkeys;
import net.blueskiez77.reach_display.utils.HUDRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReachDisplay implements ClientModInitializer {
    public static final String MOD_ID = "reach_display";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String GLOBAL_AVERAGE_FILE_NAME = "global_average_hits.txt"; // This is just here to delete the old average file. It will be removed in the next major update

    @Override
    public void onInitializeClient() {
        // ---------------------------------- Same with this block ---------------------------------- //
        Path configDir = FabricLoader.getInstance().getConfigDir();
        try {
            Files.deleteIfExists(configDir.resolve(GLOBAL_AVERAGE_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ------------------------------------------------------------------------------------------ //

        ConfigManager.load();
        HUDRenderer.register();
        Hotkeys.register();
    }
}