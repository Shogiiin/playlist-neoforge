package com.fudgeu.playlist.gui;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.widgets.ListWidget;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class SongListUI extends ListWidget<SongEntryUI> {
    private Map<ResourceLocation, Integer> entryMap = new LinkedHashMap<>();
    private final PlaylistScreen playlistScreen;
    private final Minecraft mc;
    private final int screenWidth;

    public SongListUI(PlaylistScreen playlistScreen, Minecraft client, int width, int height, int top, int bottom, int itemHeight, int screenWidth) {
        super(client, width, height, top, bottom, itemHeight);
        this.playlistScreen = playlistScreen;
        this.mc = client;
        this.refreshEntries();
        this.screenWidth = screenWidth;
    }

    @Override
    public void postRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.getEntryCount() == 0) {
            Component text = Component.translatable("screen.playlist.no_songs");
            context.drawString(
                this.mc.font, text, this.left + (this.width - this.mc.font.width(text)) / 2, (this.height + 4) / 2, 11184810, false
            );
        }
    }

    @Override
    public void renderSelected(GuiGraphics context, int x, int y, int width, int height) {
        if (!this.playlistScreen.isSelectionMode()) {
            RenderUtils.drawRect(x, y, x + width, y + height, 0.0F, 0.0F, 0.0F, 0.6F);
        }
    }

    @Override
    public void renderHovered(ListWidget.Entry<SongEntryUI> entry, GuiGraphics context, int x, int y, int width, int height) {
        RenderUtils.drawRect(x, y, x + width, y + height, 0.0F, 0.0F, 0.0F, 0.2F);
    }

    @Override
    public void checkAndUpdateFocused() {
        ResourceLocation currentSong = PlaylistClient.instance.musicManager.getCurrentSongId();
        if (currentSong != null && this.entryMap.get(currentSong) != null) {
            this.setFocused(this.getEntry(this.entryMap.get(currentSong)));
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.getRowLeft() + this.getRowWidth() + 10;
    }

    @Override
    public int getRowLeft() {
        return this.left;
    }

    @Override
    public int getRowWidth() {
        return this.screenWidth - this.getRowLeft() - 30;
    }

    public void refreshEntries() {
        this.clearEntries();
        this.entryMap = this.playlistScreen.songMap;

        for (ResourceLocation id : this.entryMap.keySet()) {
            this.addEntry(new SongEntryUI(PlaylistClient.instance.playlistScreenInstance.getSelectedPlaylist(), id));
        }
    }

    public void updateNarration(NarrationElementOutput builder) {
    }
}
