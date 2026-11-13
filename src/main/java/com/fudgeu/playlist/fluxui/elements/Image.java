package com.fudgeu.playlist.fluxui.elements;

import com.fudgeu.playlist.fluxui.FluxHelper;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.ui.TextureManager;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;

public class Image implements Element {
    private Element parent;
    private final String id;
    private final Style style;
    private final String source;
    private ResourceLocation sourceId;
    private UUID updateKey;
    private final int width;
    private final int height;
    private int memoX = 0;
    private int memoY = 0;

    public Image(String id, String source, int width, int height) {
        this.id = id;
        this.style = FluxInstance.getInstance().getStyleOrDefault(id);
        this.source = source;
        this.sourceId = FluxInstance.getInstance().getTexture(source);
        this.updateKey = TextureManager.INSTANCE.getUpdateKey();
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(GuiGraphics context, int x, int y, InheritedStyles inheritedStyles) {
        this.memoX = x;
        this.memoY = y;
        if (!this.updateKey.equals(TextureManager.INSTANCE.getUpdateKey())) {
            this.sourceId = FluxInstance.getInstance().getTexture(this.source);
            this.updateKey = TextureManager.INSTANCE.getUpdateKey();
        }

        context.setColor(1.0F, 1.0F, 1.0F, inheritedStyles.getOpacity());
        context.blit(this.sourceId, x, y, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
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
        if (FluxHelper.isInside(x, y, this.memoX, this.memoY, this.width, this.height)) {
            list.add(this);
        }

        return list;
    }

    @Override
    public Optional<String> getId() {
        return Optional.ofNullable(this.id);
    }
}
