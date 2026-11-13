package com.fudgeu.playlist.fluxui;

import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class FluxUI {
    private static Minecraft client;

    public static Screen createScreenFromElement(Element e) {
        return new FluxScreen(Component.literal("Element Screen"), e);
    }

    public static void provideMinecraftClient(Minecraft mcClient) {
        client = mcClient;
    }

    public static Optional<Minecraft> getMinecraftClient() {
        return Optional.of(client);
    }
}
