package net.wolren.reach_display;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;

public class ReachDisplay implements ClientModInitializer {
    public static final String MOD_ID = "reach_display";

    @Override
    public void onInitializeClient() {
        MidnightConfig.init(MOD_ID, DisplayConfig.class);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client ->
                SharedData.getInstance().close());
    }
}
