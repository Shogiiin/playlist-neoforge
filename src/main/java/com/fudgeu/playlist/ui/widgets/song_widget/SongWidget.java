package com.fudgeu.playlist.ui.widgets.song_widget;

import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.objects.SongObj;
import net.minecraft.resources.ResourceLocation;

public class SongWidget implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/song_widget/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public SongWidget() {
        this.propTemplate.requireString("songId");
        this.propTemplate.requireString("action");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        PropProvider childProps = new PropProvider();
        PlaylistManager playlistManager = PlaylistClient.instance.playlistManager;
        String songId = propProvider.getString("songId");
        SongObj song = playlistManager.getRegisteredSongObj(ResourceLocation.parse(songId));
        if (song == null) {
            return childProps;
        } else {
            String var9 = propProvider.getString("action");
            switch (var9) {
                case "skip":
                    childProps.setString("actionIcon", "skip");
                    break;
                case "rewind":
                    childProps.setString("actionIcon", "rewind");
                    break;
                case "play":
                    childProps.setString("actionIcon", "play");
            }

            childProps.setString("albumArt", song.getAlbumArt().toString());
            childProps.setString("songTitle", song.getTitle());
            childProps.setString("songArtist", song.getArtist());
            return childProps;
        }
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
    }

    @Override
    public void refresh() {
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return this.componentLocation;
    }
}
