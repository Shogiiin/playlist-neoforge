package com.fudgeu.playlist.objects;

import com.fudgeu.playlist.utils.GeneralUtils;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;

public class SongObj {
    private final Music song;
    private final int frequencyMultiplier;
    private final String title;
    private final String artist;
    private final String album;
    private final ResourceLocation albumArt;
    private final String altName;
    private final String type;
    private final ResourceLocation identifier;
    private final ArrayList<CategorySet> categories;

    public SongObj(
        ResourceLocation identifier,
        String title,
        String artist,
        String album,
        String altName,
        String type,
        Music song,
        int frequencyMultiplier,
        ArrayList<CategorySet> categories
    ) {
        this.identifier = identifier;
        this.title = GeneralUtils.defaultTo(title, "");
        this.artist = GeneralUtils.defaultTo(artist, "");
        this.album = GeneralUtils.defaultTo(album, "");
        this.albumArt = GeneralUtils.matchUpAlbumToArt(album);
        this.altName = altName;
        this.type = type;
        this.song = song;
        this.frequencyMultiplier = frequencyMultiplier;
        this.categories = categories;
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public Music getMusicSound() {
        return this.song;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public ResourceLocation getAlbumArt() {
        return this.albumArt;
    }

    @Nullable
    public String getAltName() {
        return this.altName;
    }

    @Nullable
    public String getType() {
        return this.type;
    }

    public int getWeight() {
        return this.frequencyMultiplier;
    }

    public ArrayList<CategorySet> getCategories() {
        return this.categories;
    }

    // Port addition: official track number on the album (0 = not on an album / unknown).
    private int trackNumber = 0;

    public int getTrackNumber() {
        return this.trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }
}
