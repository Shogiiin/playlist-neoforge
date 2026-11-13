package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;

public class TransparentButton extends Button {
    private final Minecraft mc;
    private RGBA defaultColor = new RGBA(0.1F, 0.1F, 0.1F, 1.0F);
    private RGBA hoveredColor = new RGBA(0.125F, 0.125F, 0.125F, 1.0F);
    private RGBA activeColor = new RGBA(0.1F, 0.1F, 0.1F, 1.0F);
    private int defaultTextColor = 16777215;
    private int hoveredTextColor = 16777215;
    private int activeTextColor = 16777215;
    private int disabledTextColor = 11184810;
    private int padding = 10;
    private TransparentButton.TextAlignment textAlignment = TransparentButton.TextAlignment.CENTERED;
    private TransparentButton.DisplayMode displayMode = TransparentButton.DisplayMode.TEXT_ONLY;
    public boolean forceHover = false;
    private boolean lastFrameHovered = false;
    private TransparentButton.HoverAction hoverAction;
    private ResourceLocation iconPath;
    public boolean isHalfHeight = false;
    public boolean disabled = false;

    public TransparentButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        // Icon-only buttons pass a null label; 1.21 crashes building narration text for null, so use an empty one.
        super(x, y, width, height, message != null ? message : Component.empty(), onPress, DEFAULT_NARRATION);
        this.mc = Minecraft.getInstance();
    }

    public void setDefaultColor(RGBA color) {
        this.defaultColor = color;
    }

    public void setHoveredColor(RGBA color) {
        this.hoveredColor = color;
    }

    public void setActiveColor(RGBA color) {
        this.activeColor = color;
    }

    public void setDefaultTextColor(int color) {
        this.defaultTextColor = color;
    }

    public void setHoveredTextColor(int color) {
        this.hoveredTextColor = color;
    }

    public void setActiveTextColor(int color) {
        this.activeTextColor = color;
    }

    public void setTextAlignment(TransparentButton.TextAlignment alignment) {
        this.textAlignment = alignment;
    }

    public void setIcon(ResourceLocation iconPath) {
        this.iconPath = iconPath;
    }

    public void setDisplayMode(TransparentButton.DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setStyle(TransparentButton.ButtonStyle style) {
        switch (style) {
            case DEFAULT:
                this.defaultColor = new RGBA(0.1F, 0.1F, 0.1F);
                this.hoveredColor = new RGBA(0.125F, 0.125F, 0.125F);
                this.defaultTextColor = 16777215;
                this.hoveredTextColor = 16777215;
                break;
            case TRANSPARENT:
                this.defaultColor = new RGBA(0.0F, 0.0F, 0.0F, 0.0F);
                this.hoveredColor = new RGBA(0.0F, 0.0F, 0.0F, 0.0F);
                this.activeColor = new RGBA(0.0F, 0.0F, 0.0F, 0.0F);
                this.defaultTextColor = 11184810;
                this.hoveredTextColor = 16777215;
                break;
            case HIGH_CONTRAST:
                this.defaultColor = new RGBA(0.05F, 0.05F, 0.05F, 1.0F);
                this.hoveredColor = new RGBA(0.075F, 0.075F, 0.075F, 1.0F);
                this.defaultTextColor = 16777215;
                this.hoveredTextColor = 16777215;
        }
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        int x = this.getX();
        int y = this.getY();
        this.isHovered = (this.forceHover || !PlaylistClient.instance.playlistScreenInstance.isContextMenuActive())
            && mouseX >= x
            && mouseY >= y
            && mouseX < x + this.width
            && mouseY < y + this.height;
        if (this.visible) {
            if (this.isHovered && !this.lastFrameHovered && this.hoverAction != null) {
                this.hoverAction.onHover(this);
            }

            RGBA color = this.disabled
                ? this.defaultColor
                : (this.isFocused() ? this.activeColor : (this.isHovered() ? this.hoveredColor : this.defaultColor));
            RenderUtils.drawRect(x, y, x + this.width, y + this.height, color.r, color.g, color.b, color.a);
            int drawY = y + (this.height - 8) / 2;
            int drawX;
            if (this.textAlignment == TransparentButton.TextAlignment.LEFT) {
                drawX = x + this.padding;
            } else {
                int contentLength = 0;
                if (this.displayMode == TransparentButton.DisplayMode.ICON_AND_TEXT) {
                    contentLength = this.mc.font.width(this.getMessage()) + 13;
                } else if (this.displayMode == TransparentButton.DisplayMode.ICON_ONLY) {
                    contentLength = 8;
                } else if (this.displayMode == TransparentButton.DisplayMode.TEXT_ONLY) {
                    contentLength = this.mc.font.width(this.getMessage());
                }

                drawX = x + this.width / 2 - contentLength / 2;
            }

            int hex = this.disabled
                ? this.disabledTextColor
                : (this.isFocused() ? this.activeTextColor : (this.isHovered() ? this.hoveredTextColor : this.defaultTextColor));
            if (this.displayMode == TransparentButton.DisplayMode.ICON_AND_TEXT || this.displayMode == TransparentButton.DisplayMode.ICON_ONLY) {
                context.setColor(((hex & 0xFF0000) >> 16) / 255.0F, ((hex & 0xFF00) >> 8) / 255.0F, (hex & 0xFF) / 255.0F, 1.0F);
                context.blit(this.iconPath, drawX, drawY, 0.0F, 0.0F, 8, 8, 8, this.isHalfHeight ? 4 : 8);
                context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                drawX += 13;
            }

            if (this.displayMode == TransparentButton.DisplayMode.TEXT_ONLY || this.displayMode == TransparentButton.DisplayMode.ICON_AND_TEXT) {
                context.drawString(this.mc.font, this.getMessage(), drawX, drawY, hex, false);
            }
        }

        this.lastFrameHovered = this.isHovered;
    }

    public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.setX(x);
        this.setY(y);
        this.render(context, mouseX, mouseY, partialTicks);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        } else if (!this.isValidClickButton(button) || !this.clicked(mouseX, mouseY)) {
            return false;
        } else if (this.disabled) {
            return true;
        } else {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick(mouseX, mouseY);
            return true;
        }
    }

    public void overrideFocusedStatus(boolean newState) {
        this.setFocused(newState);
    }

    public void setDisabledTextColor(int disabledTextColor) {
        this.disabledTextColor = disabledTextColor;
    }

    public void setHoverAction(TransparentButton.HoverAction hoverAction) {
        this.hoverAction = hoverAction;
    }

    public static enum ButtonStyle {
        DEFAULT,
        TRANSPARENT,
        HIGH_CONTRAST;
    }

    public static enum DisplayMode {
        TEXT_ONLY,
        ICON_ONLY,
        ICON_AND_TEXT;
    }

    public interface HoverAction {
        void onHover(TransparentButton var1);
    }

    public static enum TextAlignment {
        LEFT,
        CENTERED;
    }
}
