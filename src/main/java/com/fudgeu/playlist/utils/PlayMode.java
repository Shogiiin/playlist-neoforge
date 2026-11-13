package com.fudgeu.playlist.utils;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.PlaylistScreen;

public class PlayMode {
    private Mode playMode = Mode.CATEGORY_BASED;
    private PlaylistScreen playlistScreen;

    public void set(Mode mode) {
        this.playMode = mode;
        this.playlistScreen = PlaylistClient.instance.playlistScreenInstance;
        if (this.playlistScreen != null) {
            this.playlistScreen.updatePlayMode(mode);
        }
    }

    public Mode getPlayMode() {
        return this.playMode;
    }
}
