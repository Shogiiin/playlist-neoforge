package com.fudgeu.playlist.fluxui;

import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class FluxWidget {
    private final Minecraft minecraftClient;
    private final InheritedStyles defaultInheritedStyles;
    private Element widget;
    private FluxWidget.AnchorPoint anchorPoint;
    private int padding = 20;

    public FluxWidget(Element widget, FluxWidget.AnchorPoint anchorPoint) {
        this.minecraftClient = Minecraft.getInstance();
        this.defaultInheritedStyles = new InheritedStyles(SolidColor.WHITE, 1.0F, 0, 1.0F, 1.0F);
        this.widget = widget;
        this.anchorPoint = anchorPoint;
    }

    public void render(GuiGraphics context) {
        if (this.widget != null) {
            int width = this.widget.getWidth();
            int height = this.widget.getHeight();
            int[] position = this.determinePosition(width, height, this.padding, this.anchorPoint);
            int xPos = position[0];
            int yPos = position[1];
            this.widget.draw(context, xPos, yPos, this.defaultInheritedStyles);
        }
    }

    private int[] determinePosition(int widgetWidth, int widgetHeight, int padding, FluxWidget.AnchorPoint anchorPoint) {
        int[] position = new int[]{0, 0};
        int screenWidth = this.minecraftClient.getWindow().getGuiScaledWidth();
        int screenHeight = this.minecraftClient.getWindow().getGuiScaledHeight();
        switch (anchorPoint) {
            case BOTTOM_RIGHT_CORNER:
                position[0] = screenWidth - widgetWidth - padding;
                position[1] = screenHeight - widgetHeight - padding;
            default:
                return position;
        }
    }

    public Element getWidget() {
        return this.widget;
    }

    public void setWidget(Element widget) {
        this.widget = widget;
    }

    public FluxWidget.AnchorPoint getAnchorPoint() {
        return this.anchorPoint;
    }

    public void setAnchorPoint(FluxWidget.AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    public int getPadding() {
        return this.padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public static enum AnchorPoint {
        TOP_LEFT_CORNER,
        TOP_RIGHT_CORNER,
        BOTTOM_LEFT_CORNER,
        BOTTOM_RIGHT_CORNER,
        TOP_CENTER,
        LEFT_CENTER,
        RIGHT_CENTER,
        BOTTOM_CENTER,
        CENTER;
    }
}
