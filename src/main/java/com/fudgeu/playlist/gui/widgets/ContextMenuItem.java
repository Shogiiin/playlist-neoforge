package com.fudgeu.playlist.gui.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.Button.OnPress;

public class ContextMenuItem {
    public ResourceLocation iconPath;
    public Component text;
    public boolean enabled;
    public OnPress onPress;

    public ContextMenuItem(ResourceLocation iconPath, Component text, boolean enabled, OnPress onPress) {
        this.iconPath = iconPath;
        this.text = text;
        this.enabled = enabled;
        this.onPress = onPress;
    }
}
