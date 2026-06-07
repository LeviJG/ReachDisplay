package net.blueskiez77.reach_display;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.blueskiez77.reach_display.config.DisplayConfig;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ReachDisplay implements ClientModInitializer {
    public static final String MOD_ID = "reach_display";
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

        MidnightConfig.init(MOD_ID, DisplayConfig.class);
    }
}