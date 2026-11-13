package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.utils.RGBA;
import java.util.LinkedHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class TabSelector implements GuiEventListener, Renderable, NarratableEntry {
    private int x;
    private int y;
    private int endX;
    private int endY;
    private LinkedHashMap<String, String> tabMap;
    private LinkedHashMap<String, TransparentButton> buttons = new LinkedHashMap<>();
    private String currentTab;
    private Minecraft mc;

    public TabSelector(int x, int y, int endX, int endY, LinkedHashMap<String, String> tabs, String defaultTab) {
        this.tabMap = tabs;
        this.currentTab = defaultTab;
        this.mc = Minecraft.getInstance();
        int xCount = x;

        for (String tabId : tabs.keySet()) {
            int buttonWidth = this.mc.font.width(tabs.get(tabId)) + 14;
            TransparentButton button = new TransparentButton(
                xCount, y, buttonWidth, 20, Component.literal(tabs.get(tabId)), onPress -> this.currentTab = tabId
            );
            button.setDefaultTextColor(11184810);
            button.setHoveredTextColor(11184810);
            button.setActiveTextColor(16777215);
            button.setActiveColor(new RGBA(0.15F));
            this.buttons.put(tabId, button);
            xCount += buttonWidth;
        }
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.currentTab != null) {
            for (TransparentButton button : this.buttons.values()) {
                button.overrideFocusedStatus(false);
            }

            this.buttons.get(this.currentTab).overrideFocusedStatus(true);
        }

        for (TransparentButton button : this.buttons.values()) {
            button.render(context, mouseX, mouseY, delta);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TransparentButton tabButton : this.buttons.values()) {
            boolean clicked = tabButton.mouseClicked(mouseX, mouseY, button);
            if (clicked) {
                PlaylistClient.instance.playlistScreenInstance.refreshSongList();
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

    public String getSelectedTab() {
        return this.currentTab;
    }

    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput builder) {
    }
}
