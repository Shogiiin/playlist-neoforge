package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.utils.RGBA;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class Texture implements GuiEventListener, Renderable, NarratableEntry {
    private int x;
    private int y;
    private int w;
    private int h;
    private ResourceLocation texture;
    private RGBA color;

    public Texture(int x, int y, int w, int h, ResourceLocation texture) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.texture = texture;
        this.color = new RGBA(1.0F);
    }

    public Texture(int x, int y, int w, int h, ResourceLocation texture, RGBA color) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.texture = texture;
        this.color = color;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.setColor(this.color.r, this.color.g, this.color.b, this.color.a);
        context.blit(this.texture, this.x, this.y, 0.0F, 0.0F, this.w, this.h, this.w, this.h);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
