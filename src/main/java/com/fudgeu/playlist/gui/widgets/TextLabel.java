package com.fudgeu.playlist.gui.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class TextLabel implements GuiEventListener, Renderable, NarratableEntry {
    private int x;
    private int y;
    private Component label;
    private int color;
    private Minecraft mc;
    public boolean visible = true;

    public TextLabel(int x, int y, Component label, int color) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.color = color;
        this.mc = Minecraft.getInstance();
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            context.drawString(this.mc.font, this.label, this.x, this.y, this.color, false);
        }
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }

    public void setLabel(Component label) {
        this.label = label;
    }
}
