package com.fudgeu.playlist.gui;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.widgets.ListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class PlaylistSelector extends ListWidget {
    private PlaylistScreen playlistScreen;
    private Minecraft mc;

    public PlaylistSelector(PlaylistScreen playlistScreen, Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.playlistScreen = playlistScreen;
        this.mc = client;
        this.refreshEntries();
        if (playlistScreen.getSelectedPlaylist() != -1) {
        }
    }

    public void refreshEntries() {
        for (Integer id : PlaylistClient.instance.playlistManager.getRegisteredPlaylists()) {
            this.addEntry(new PlaylistEntry(this.playlistScreen, id));
        }
    }

    public void updateBottom(int bottom) {
        this.height = bottom - this.top;
        this.bottom = bottom;
    }

    @Override
    public void checkAndUpdateFocused() {
        if (this.playlistScreen.getSelectedPlaylist() == -1) {
            this.setFocused(null);
        } else {
            int currentPlaylist = PlaylistClient.instance.playlistScreenInstance.getSelectedPlaylist();
            this.setFocused(this.getEntry(currentPlaylist));
        }
    }

    public void updateNarration(NarrationElementOutput builder) {
    }
}
