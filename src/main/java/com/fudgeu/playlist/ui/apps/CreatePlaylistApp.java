package com.fudgeu.playlist.ui.apps;

import net.minecraft.resources.ResourceLocation;

public class CreatePlaylistApp extends PlaylistApp {
    @Override
    public void refresh() {
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/playlist_app/create_playlist_app.xml");
    }
}
