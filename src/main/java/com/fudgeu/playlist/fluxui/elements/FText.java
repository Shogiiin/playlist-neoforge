package com.fudgeu.playlist.fluxui.elements;

import com.fudgeu.playlist.fluxui.FluxHelper;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxUI;
import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class FText implements Element {
    private final String id;
    private Component text;
    private Style style;
    private Element parent;
    private final Minecraft client;
    private boolean isDirty = true;
    private int memoX = 0;
    private int memoY = 0;
    private int memoWidth = 0;
    private int memoHeight = 0;

    public FText(String id, Component text) {
        this.id = id;
        this.style = FluxInstance.getInstance().getStyleOrDefault(id);
        this.text = text;
        this.client = FluxUI.getMinecraftClient().orElseThrow();
    }

    public Component getText() {
        return this.text;
    }

    public void setText(Component newText) {
        this.text = newText;
        this.isDirty = true;
    }

    @Override
    public void draw(GuiGraphics context, int x, int y, InheritedStyles inheritedStyles) {
        this.memoX = x;
        this.memoY = y;
        SolidColor foregroundColor = this.style.getForegroundColor().orElse(inheritedStyles.getForegroundColor());
        int hex = foregroundColor.getColor().hex();
        int mixedOpacity = Math.round(255.0F * inheritedStyles.getOpacity()) << 24;
        mixedOpacity = (int)Math.max(Integer.toUnsignedLong(mixedOpacity), 268435456L);
        context.drawString(this.client.font, this.text, x, y, hex + mixedOpacity, false);
    }

    @Override
    public int getWidth() {
        if (!this.isDirty) {
            return this.memoWidth;
        } else {
            this.isDirty = false;
            return this.memoWidth = this.client.font.width(this.text);
        }
    }

    @Override
    public int getHeight() {
        this.memoHeight = 8;
        return 8;
    }

    @Override
    public void setParent(Element parent) {
        this.parent = parent;
    }

    @Override
    public Optional<Element> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    @Override
    public ArrayList<Element> getElementsAt(int x, int y) {
        ArrayList<Element> list = new ArrayList<>();
        if (FluxHelper.isInside(x, y, this.memoX, this.memoY, this.memoWidth, this.memoHeight)) {
            list.add(this);
        }

        return list;
    }

    @Override
    public Optional<String> getId() {
        return Optional.ofNullable(this.id);
    }
}
