package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class ContextMenu implements GuiEventListener, Renderable, NarratableEntry {
    private final Minecraft mc;
    private int startX;
    private int startY;
    private final int mouseX;
    private final int mouseY;
    private int width;
    private int height;
    private final List<ContextMenu.CloseAction> closeActions = new ArrayList<>();
    private final List<TransparentButton> options = new ArrayList<>();
    private final List<ContextMenuItem> buildItems = new ArrayList<>();

    public ContextMenu(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        this.mc = Minecraft.getInstance();
    }

    public void addOption(ResourceLocation iconPath, Component text, boolean enabled, OnPress onPress) {
        this.buildItems.add(new ContextMenuItem(iconPath, text, enabled, onPress));
    }

    public void build() {
        int longestTitle = 0;

        for (ContextMenuItem item : this.buildItems) {
            int itemTextLength = item.text.getString().length();
            if (itemTextLength > longestTitle) {
                longestTitle = itemTextLength;
            }
        }

        this.width = Math.max(125, longestTitle * 6 + 20);
        this.height = this.buildItems.size() * 20 + 2;
        if (this.mouseX + this.width > this.mc.screen.width) {
            this.startX = this.mouseX - this.width;
        } else {
            this.startX = this.mouseX;
        }

        if (this.mouseY + this.height > this.mc.screen.height) {
            this.startY = this.mouseY - this.height;
        } else {
            this.startY = this.mouseY;
        }

        for (ContextMenuItem itemx : this.buildItems) {
            TransparentButton option = new TransparentButton(this.startX, this.startY + this.options.size() * 20, this.width, 20, itemx.text, itemx.onPress);
            if (itemx.iconPath != null) {
                option.setIcon(itemx.iconPath);
                option.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
            }

            option.setPadding(5);
            option.setTextAlignment(TransparentButton.TextAlignment.LEFT);
            option.setDefaultColor(new RGBA(0.1F, 1.0F));
            option.setHoveredColor(new RGBA(0.125F, 1.0F));
            option.forceHover = true;
            option.disabled = !itemx.enabled;
            this.options.add(option);
        }
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawRect(this.startX - 1, this.startY - 1, this.startX + this.width + 1, this.startY + this.options.size() * 20 + 1, 0.5F, 0.5F, 0.5F, 1.0F);

        for (TransparentButton option : this.options) {
            option.render(context, mouseX, mouseY, delta);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TransparentButton option : this.options) {
            boolean clicked = option.mouseClicked(mouseX, mouseY, button);
            if (clicked) {
                this.close();
                return true;
            }
        }

        return false;
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }

    public void close() {
        for (ContextMenu.CloseAction closeAction : this.closeActions) {
            closeAction.onClose(this);
        }
    }

    public void addCloseAction(ContextMenu.CloseAction closeAction) {
        this.closeActions.add(closeAction);
    }

    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    public interface CloseAction {
        void onClose(ContextMenu var1);
    }
}
