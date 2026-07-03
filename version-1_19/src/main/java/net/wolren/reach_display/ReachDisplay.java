package net.wolren.reach_display;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.wolren.reach_display.config.DisplayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReachDisplay implements ModInitializer {
    public static final String MOD_ID = "reach_display";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MidnightConfig.init(ReachDisplay.MOD_ID, DisplayConfig.class);
    }
}
