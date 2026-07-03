package net.wolren.reach_display;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.wolren.reach_display.config.DisplayConfig;
import net.wolren.reach_display.data.SharedData;

public class ReachDisplayClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
            SharedData.getInstance().close();
        });
        DisplayConfig.init("reach_display", DisplayConfig.class);
    }
}
