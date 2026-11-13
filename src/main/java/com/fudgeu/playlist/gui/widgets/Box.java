package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class Box implements GuiEventListener, Renderable, NarratableEntry {
    private int x;
    private int y;
    private int endX;
    private int endY;
    private RGBA color;

    public Box(int x, int y, int endX, int endY, RGBA color) {
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawRect(this.x, this.y, this.endX, this.endY, this.color.r, this.color.g, this.color.b, this.color.a);
    }

    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }
}
