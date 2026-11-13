package com.fudgeu.playlist.gui.screens.createplaylist;

import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.widgets.RadioGroup;
import com.fudgeu.playlist.objects.PlaylistObj;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public class CreatePlaylistScreen extends MenuScreen {
    private final ToastManager toastManager;
    private EditBox playlistNameField;
    private RadioGroup baseSelection;

    public CreatePlaylistScreen(Screen previousScreen) {
        super(Component.translatable("screen.create_playlist.title"), previousScreen);
        this.toastManager = PlaylistClient.instance.toastManager;
    }

    @Override
    public void init() {
        this.setMainContentHeight(50);
        super.init();
        this.playlistNameField = this.addTextBox(0, 16, this.menuWidth, 20, Component.literal(""));
        this.addTextLabel(0, 0, Component.translatable("screen.create_playlist.playlist_name"), 16777215);
        this.baseSelection = this.addRadioGroup(0, 46, this.menuWidth, 12);
        this.baseSelection.addRadioButton(0, 0, "base_off_default", Component.translatable("screen.create_playlist.base_off_default"));
        this.setPrimaryMenuButton(Component.translatable("screen.create_playlist.create"), onPress -> {
            PlaylistObj newPlaylist = new PlaylistObj(this.playlistNameField.getValue());
            if (this.baseSelection.getSelectedButtons().contains("base_off_default")) {
                for (ResourceLocation songId : PlaylistClient.instance.playlistManager.getRegisteredSongs()) {
                    newPlaylist.addSong(songId);
                }
            }

            PlaylistClient.instance.playlistManager.registerPlaylist(newPlaylist);
            this.closeScreen();
            this.toastManager.addToast(Component.translatable("toast.playlist_created"), 60);
        });
    }

    @Override
    public void tick() {
        super.tick();
    }
}
