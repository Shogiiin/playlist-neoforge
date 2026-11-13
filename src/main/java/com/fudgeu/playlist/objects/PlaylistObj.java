package com.fudgeu.playlist.objects;

import com.fudgeu.playlist.ConditionManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class PlaylistObj {
    private List<ResourceLocation> playlistItems = new ArrayList<>();
    private String identifier;
    private String title;
    private String uniqueId;
    private int menuLeadTime;
    private int menuTimeBetweenSongs;
    private int gameLeadTime;
    private int timeBetweenSongs;
    private int timeVariance;
    private HashMap<ResourceLocation, ArrayList<CategorySet>> customCategories = new HashMap<>();
    private HashMap<ResourceLocation, Integer> customWeights = new HashMap<>();
    private StateManager stateManager;

    public PlaylistObj(List<ResourceLocation> playlistItems, String title) {
        for (ResourceLocation songId : playlistItems) {
            this.addSong(songId);
        }

        this.title = title;
        this.menuLeadTime = 0;
        this.menuTimeBetweenSongs = 30;
        this.gameLeadTime = 30;
        this.timeBetweenSongs = 900;
        this.timeVariance = 300;
        this.stateManager = PlaylistClient.instance.stateManager;
    }

    public PlaylistObj(String title) {
        this.title = title;
        this.menuLeadTime = 0;
        this.menuTimeBetweenSongs = 30;
        this.gameLeadTime = 30;
        this.timeBetweenSongs = 900;
        this.timeVariance = 300;
        this.stateManager = PlaylistClient.instance.stateManager;
    }

    public void setUniqueId(String UUID) {
        this.uniqueId = UUID;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public void setTimeBetweenSongs(int time) {
        this.timeBetweenSongs = time;
    }

    public void setTimeVariance(int variance) {
        this.timeVariance = variance;
    }

    public int getTimeBetweenSongs() {
        return this.timeBetweenSongs;
    }

    public int getTimeVariance() {
        return this.timeVariance;
    }

    public void loadSong(ResourceLocation song) {
        this.playlistItems.add(song);
    }

    public void addSong(ResourceLocation song) {
        this.playlistItems.add(song);
        this.customCategories.put(song, (ArrayList<CategorySet>)PlaylistClient.instance.playlistManager.getRegisteredSongObj(song).getCategories().clone());
        this.customWeights.put(song, PlaylistClient.instance.playlistManager.getRegisteredSongObj(song).getWeight());
    }

    public void removeSong(ResourceLocation song) {
        this.playlistItems.remove(song);
    }

    public ResourceLocation getSong(int index) {
        return this.playlistItems.get(index);
    }

    public int getSongCount() {
        return this.playlistItems.size();
    }

    public ResourceLocation getRandomSong() {
        return this.playlistItems.get(new Random().nextInt(0, this.playlistItems.size()));
    }

    public ResourceLocation getRandomSongBasedOnConditions() {
        ResourceLocation songId = null;
        ConditionManager conditionManager = PlaylistClient.instance.conditionManager;
        PlaylistManager playlistManager = PlaylistClient.instance.playlistManager;
        ArrayList<ResourceLocation> weightedSongList = new ArrayList<>();

        for (ResourceLocation song : this.playlistItems) {
            int songWeight = this.customWeights.get(song) != null ? this.customWeights.get(song) : playlistManager.getRegisteredSongObj(song).getWeight();

            for (int i = 1; i <= songWeight; i++) {
                weightedSongList.add(song);
            }
        }

        for (int i = 0; i < 500; i++) {
            songId = weightedSongList.get(new Random().nextInt(0, weightedSongList.size()));
            SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(songId);
            if (song.getCategories().size() != 0) {
                ArrayList<CategorySet> categorySets = this.customCategories.get(songId);
                if (categorySets == null) {
                    categorySets = song.getCategories();
                }

                for (CategorySet categorySet : categorySets) {
                    String category = categorySet.getCategoryId();
                    if (!conditionManager.exists(category)) {
                        break;
                    }

                    if (conditionManager.check(category)) {
                        if (!categorySet.hasSubcategories()) {
                            this.stateManager.setStateString("lastSelectedCategory", category);
                            this.stateManager.setStateString("lastSelectedCondition", null);
                            return songId;
                        }

                        for (String subcategory : categorySet.getSubcategories()) {
                            if (!conditionManager.exists(subcategory)) {
                                break;
                            }

                            if (conditionManager.check(subcategory)) {
                                this.stateManager.setStateString("lastSelectedCategory", category);
                                this.stateManager.setStateString("lastSelectedCondition", subcategory);
                                return songId;
                            }
                        }
                    }
                }
            }
        }

        return songId;
    }

    public ResourceLocation getRandomSongBasedOn(String conditionId) {
        ResourceLocation songId = null;
        ConditionManager conditionManager = PlaylistClient.instance.conditionManager;
        PlaylistManager playlistManager = PlaylistClient.instance.playlistManager;
        ArrayList<ResourceLocation> weightedSongList = new ArrayList<>();

        for (ResourceLocation song : this.playlistItems) {
            int songWeight = this.customWeights.get(song) != null ? this.customWeights.get(song) : playlistManager.getRegisteredSongObj(song).getWeight();

            for (int i = 1; i <= songWeight; i++) {
                weightedSongList.add(song);
            }
        }

        for (int i = 0; i < 500; i++) {
            songId = weightedSongList.get(new Random().nextInt(0, weightedSongList.size()));
            SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(songId);
            if (song.getCategories().size() != 0) {
                ArrayList<CategorySet> categorySets = this.customCategories.get(songId);
                if (categorySets == null) {
                    categorySets = song.getCategories();
                }

                for (CategorySet categorySet : categorySets) {
                    String categoryId = categorySet.getCategoryId();
                    if (!conditionManager.exists(categoryId)) {
                        break;
                    }

                    if (categoryId.equals(conditionId)) {
                        this.stateManager.setStateString("lastSelectedCategory", categoryId);
                        this.stateManager.setStateString("lastSelectedCondition", null);
                        return songId;
                    }

                    if (conditionManager.check(categoryId) && categorySet.getSubcategories().contains(conditionId)) {
                        this.stateManager.setStateString("lastSelectedCategory", categoryId);
                        this.stateManager.setStateString("lastSelectedCondition", conditionId);
                        return songId;
                    }
                }
            }
        }

        return songId;
    }

    public boolean setCategorySets(ResourceLocation songId, ArrayList<CategorySet> categorySets) {
        if (!this.playlistItems.contains(songId)) {
            return false;
        } else {
            this.customCategories.put(songId, categorySets);
            return true;
        }
    }

    public ArrayList<CategorySet> getCategorySets(ResourceLocation songId) {
        return this.customCategories.get(songId);
    }

    public void setCustomWeight(ResourceLocation songId, int newWeight) {
        this.customWeights.remove(songId);
        this.customWeights.put(songId, newWeight);
    }

    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("title", this.title);
        if (this.uniqueId == null) {
            this.uniqueId = UUID.randomUUID().toString();
        }

        json.addProperty("uuid", this.uniqueId);
        JsonObject options = new JsonObject();
        options.addProperty("timeBetweenSongs", this.timeBetweenSongs);
        options.addProperty("timeVariance", this.timeVariance);
        json.add("options", options);
        JsonArray contents = new JsonArray();

        for (ResourceLocation id : this.playlistItems) {
            contents.add(id.toString());
        }

        json.add("contents", contents);
        JsonObject customizations = new JsonObject();
        JsonObject categorySets = new JsonObject();
        JsonObject weights = new JsonObject();

        for (ResourceLocation songId : this.customCategories.keySet()) {
            JsonArray sets = new JsonArray();

            for (CategorySet set : this.customCategories.get(songId)) {
                sets.add(set.serialize());
            }

            categorySets.add(songId.toString(), sets);
        }

        for (ResourceLocation songId : this.customWeights.keySet()) {
            weights.addProperty(songId.toString(), this.customWeights.get(songId));
        }

        customizations.add("categorySets", categorySets);
        customizations.add("weights", weights);
        json.add("customizations", customizations);
        return json;
    }

    public List<ResourceLocation> getPlaylistItems() {
        return this.playlistItems;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getTitle() {
        return this.title;
    }

    public int getMenuLeadTime() {
        return this.menuLeadTime;
    }

    public int getMenuTimeBetweenSongs() {
        return this.menuTimeBetweenSongs;
    }

    public int getGameLeadTime() {
        return this.gameLeadTime;
    }

    public StateManager getStateManager() {
        return this.stateManager;
    }

    public void setPlaylistItems(List<ResourceLocation> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMenuLeadTime(int menuLeadTime) {
        this.menuLeadTime = menuLeadTime;
    }

    public void setMenuTimeBetweenSongs(int menuTimeBetweenSongs) {
        this.menuTimeBetweenSongs = menuTimeBetweenSongs;
    }

    public void setGameLeadTime(int gameLeadTime) {
        this.gameLeadTime = gameLeadTime;
    }

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public HashMap<ResourceLocation, ArrayList<CategorySet>> getCustomCategories() {
        return this.customCategories;
    }

    public void setCustomCategories(HashMap<ResourceLocation, ArrayList<CategorySet>> customCategories) {
        this.customCategories = customCategories;
    }

    public HashMap<ResourceLocation, Integer> getCustomWeights() {
        return this.customWeights;
    }

    public void setCustomWeights(HashMap<ResourceLocation, Integer> customWeights) {
        this.customWeights = customWeights;
    }
}
