package net.blueskiez77.reach_display.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.blueskiez77.reach_display.ui.MenuScreen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new MenuScreen(Component.literal("Reach Display Menu"));   // ModMenu passes the parent screen, you return the config screen
    }
}
