package com.fudgeu.playlist.gui.screens.playlistoptions;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.objects.PlaylistObj;
import com.fudgeu.playlist.utils.GeneralUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public class PlaylistOptionsScreen extends MenuScreen {
    private int playlistId;
    private EditBox playlistNameField;
    private EditBox timeBetweenSongsField;
    private EditBox timeVarianceField;

    public PlaylistOptionsScreen(Screen previousScreen, int playlistId) {
        super(Component.translatable("screen.playlist_options.title"), previousScreen);
        this.playlistId = playlistId;
    }

    @Override
    public void init() {
        this.setMainContentHeight(86);
        super.init();
        PlaylistObj playlist = PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.playlistId);
        this.addTextLabel(0, 0, Component.translatable("screen.playlist_options.playlist_name"), 16777215);
        this.playlistNameField = this.addTextBox(0, 16, this.menuWidth, 20, Component.translatable("screen.playlist_options.playlist_title"));
        this.playlistNameField.setValue(playlist.getTitle());
        this.addTextLabel(0, 47, Component.translatable("screen.playlist_options.time_between_songs"), 16777215);
        this.timeBetweenSongsField = this.addTextBox(this.menuWidth - 50, 41, 50, 20, Component.literal("0"));
        this.timeBetweenSongsField.setValue(String.valueOf(playlist.getTimeBetweenSongs()));
        this.addTextLabel(0, 72, Component.translatable("screen.playlist_options.time_variance"), 16777215);
        this.timeVarianceField = this.addTextBox(this.menuWidth - 50, 66, 50, 20, Component.literal("0"));
        this.timeVarianceField.setValue(String.valueOf(playlist.getTimeVariance()));
        this.setPrimaryMenuButton(Component.translatable("generic.save"), onPress -> {
            playlist.setTitle(GeneralUtils.defaultTo(this.playlistNameField.getValue(), "Playlist"));
            playlist.setTimeBetweenSongs(Integer.parseInt(GeneralUtils.defaultTo(this.timeBetweenSongsField.getValue(), "900")));
            playlist.setTimeVariance(Integer.parseInt(GeneralUtils.defaultTo(this.timeVarianceField.getValue(), "300")));
            this.closeScreen();
        });
    }
}
