package com.fudgeu.playlist;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.PlaylistObj;
import com.fudgeu.playlist.objects.SongObj;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.Music;
import net.minecraft.core.Holder;
import org.apache.commons.io.FileUtils;

public class PlaylistManager {
    private Map<Integer, PlaylistObj> registeredPlaylists = new LinkedHashMap<>();
    private PlaylistObj allSongsPlaylist;
    private final Map<ResourceLocation, SongObj> registeredSongs = new LinkedHashMap<>();
    private int defaultPlaylist = 0;

    public void finishInit() {
        this.allSongsPlaylist = new PlaylistObj(this.getRegisteredSongs(), "All Songs");
        this.allSongsPlaylist.setCustomCategories(ConditionManager.getDefaultCategories());
    }

    public void registerSong(
        String songID, String name, String artist, String album, String altName, String type, int weight, ArrayList<CategorySet> categories
    ) {
        ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath("playlist", songID);
        this.registeredSongs
            .put(
                identifier,
                new SongObj(
                    identifier,
                    name,
                    artist,
                    album,
                    altName,
                    type,
                    new Music(Holder.direct(SoundEvent.createVariableRangeEvent(identifier)), 20, 600, true),
                    weight,
                    categories
                )
            );
    }

    public int registerPlaylist(PlaylistObj playlist) {
        this.registeredPlaylists.put(this.registeredPlaylists.size(), playlist);
        return this.registeredPlaylists.size() - 1;
    }

    public void removeRegisteredPlaylist(int id) {
        this.registeredPlaylists.remove(id);
        this.reorganizePlaylists();
    }

    public void removeSongFromPlaylist(int playlistId, ResourceLocation songId) {
        this.registeredPlaylists.get(playlistId).removeSong(songId);
    }

    public List<Integer> getRegisteredPlaylists() {
        return new ArrayList<>(this.registeredPlaylists.keySet());
    }

    public List<ResourceLocation> getRegisteredSongs() {
        return new ArrayList<>(this.registeredSongs.keySet());
    }

    public PlaylistObj getRegisteredPlaylist(int id) {
        if (id == -1) {
            return this.allSongsPlaylist;
        } else if (!this.registeredPlaylists.containsKey(id)) {
            throw new RuntimeException("PlaylistNotFound");
        } else {
            return this.registeredPlaylists.get(id);
        }
    }

    public boolean doesPlaylistExist(int id) {
        return this.registeredPlaylists.containsKey(id);
    }

    public SongObj getRegisteredSongObj(ResourceLocation id) {
        return this.registeredSongs.get(id);
    }

    public boolean addSongToPlaylist(int playlistId, ResourceLocation songId) {
        if (!this.registeredPlaylists.get(playlistId).getPlaylistItems().contains(songId)) {
            this.registeredPlaylists.get(playlistId).addSong(songId);
            return true;
        } else {
            return false;
        }
    }

    public int getSongCount(int id) {
        return id == -1 ? this.registeredSongs.size() : this.getRegisteredPlaylist(id).getSongCount();
    }

    public int getSafeDefaultPlaylist() {
        if (this.registeredPlaylists.get(this.defaultPlaylist) == null || this.registeredPlaylists.get(this.defaultPlaylist).getSongCount() == 0) {
            this.defaultPlaylist = -1;
        }

        return this.defaultPlaylist;
    }

    public void saveAll() {
        try {
            FileUtils.deleteDirectory(new File(PlaylistClient.gameDirectory + "/playlist/playlists/"));
            Files.createDirectory(Path.of(PlaylistClient.gameDirectory + "/playlist/playlists"));
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        for (PlaylistObj playlist : this.registeredPlaylists.values()) {
            JsonObject json = playlist.getAsJson();

            try {
                File file = new File(PlaylistClient.gameDirectory + "/playlist/playlists/" + playlist.getUniqueId() + ".json");
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(
                    new FileWriter(PlaylistClient.gameDirectory + "/playlist/playlists/" + playlist.getUniqueId() + ".json")
                );
                String serializedJson = new GsonBuilder().setPrettyPrinting().create().toJson(json);
                writer.write(serializedJson);
                writer.close();
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

        try {
            File file = new File(PlaylistClient.gameDirectory + "/playlist/playlist_order.json");
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(PlaylistClient.gameDirectory + "/playlist/playlist_order.json"));
            JsonObject json = new JsonObject();
            JsonArray order = new JsonArray();

            for (PlaylistObj playlist : this.registeredPlaylists.values()) {
                order.add(playlist.getUniqueId());
            }

            json.add("order", order);
            int safeDefault = this.getSafeDefaultPlaylist();
            if (safeDefault != -1) {
                json.addProperty("default", this.registeredPlaylists.get(safeDefault).getUniqueId());
            }

            String serializedJson = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            writer.write(serializedJson);
            writer.close();
        } catch (IOException var9) {
            var9.printStackTrace();
        }
    }

    private void reorganizePlaylists() {
        Map<Integer, PlaylistObj> newList = new LinkedHashMap<>();
        int i = 0;

        for (PlaylistObj playlist : this.registeredPlaylists.values()) {
            newList.put(i, playlist);
            i++;
        }

        this.registeredPlaylists = newList;
    }

    public void organizeBasedOn(ArrayList<String> UUIDs) {
        Map<Integer, PlaylistObj> newPlaylists = new LinkedHashMap<>();
        int i = 0;

        for (String UUID : UUIDs) {
            for (int playlistId : this.registeredPlaylists.keySet()) {
                PlaylistObj playlistObj = this.registeredPlaylists.get(playlistId);
                if (playlistObj.getUniqueId().equals(UUID)) {
                    newPlaylists.put(i, playlistObj);
                    break;
                }
            }

            i++;
        }

        for (PlaylistObj playlistObj : this.registeredPlaylists.values()) {
            if (!newPlaylists.containsValue(playlistObj)) {
                newPlaylists.put(i, playlistObj);
                i++;
            }
        }

        this.registeredPlaylists = newPlaylists;
    }

    public boolean setDefaultPlaylist(int id) {
        if (this.registeredPlaylists.containsKey(id)) {
            this.defaultPlaylist = id;
            return true;
        } else {
            return false;
        }
    }

    public boolean setDefaultPlaylist(String UUID) {
        for (int id : this.registeredPlaylists.keySet()) {
            if (this.registeredPlaylists.get(id).getUniqueId().equals(UUID)) {
                this.defaultPlaylist = id;
                return true;
            }
        }

        return false;
    }

    public int getDefaultPlaylist() {
        return this.defaultPlaylist;
    }
}
