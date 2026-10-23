package net.blueskiez77.reach_display.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.blueskiez77.reach_display.ReachDisplay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/// Rebindable hotkeys (Options > Controls > Key Binds > Reach Display)
public class Hotkeys {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ReachDisplay.MOD_ID, "reach_display"));

    // G by default
    private static final KeyMapping OPEN_HUD_CONFIGURATOR = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.reach_display.open_hud_configurator", InputConstants.Type.KEYSYM, InputConstants.KEY_G, CATEGORY));

    // Unbound by default; the HUD configurator's "More Options" button already leads here
    private static final KeyMapping OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.reach_display.open_menu", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Key mappings only register presses while no screen is open, so these never open over another menu
            while (OPEN_HUD_CONFIGURATOR.consumeClick()) {
                client.setScreenAndShow(new HUDConfig(() -> ConfigGUI.create(null)));
            }
            while (OPEN_MENU.consumeClick()) {
                client.setScreenAndShow(ConfigGUI.create(null));
            }
        });
    }
}
