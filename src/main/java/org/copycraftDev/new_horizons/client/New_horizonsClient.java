package org.copycraftDev.new_horizons.client;

import foundry.veil.Veil;
import foundry.veil.fabric.event.FabricFreeNativeResourcesEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class New_horizonsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Veil.init();

        KeyBinding flashlightKeyBinding = new KeyBinding(
                "key.new_horizons.flashlight",   // Translation key
                GLFW.GLFW_KEY_F,                // Default key
                "category.new_horizons"         // Keybinding category
        );
        KeyBindingHelper.registerKeyBinding(flashlightKeyBinding);

    }
}
