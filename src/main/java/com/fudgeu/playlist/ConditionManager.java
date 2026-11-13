package com.fudgeu.playlist;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.SongObj;
import com.fudgeu.playlist.utils.GeneralUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;

public class ConditionManager {
    private LinkedHashMap<String, Condition> registeredConditions = new LinkedHashMap<>();
    private Map<String, Boolean> activelyCheckedConditions = new HashMap<>();
    private Minecraft mc = Minecraft.getInstance();
    private StateManager stateManager;

    public ConditionManager() {
        this.stateManager = PlaylistClient.instance.stateManager;
    }

    public void registerCondition(String id, boolean isCategory, Condition.ConditionCheck conditionCheck) {
        this.registeredConditions.put(id, new Condition(id, isCategory, conditionCheck));
    }

    public void registerActiveCondition(String id, boolean isCategory, Condition.ConditionCheck conditionCheck) {
        this.registeredConditions.put(id, new Condition(id, isCategory, conditionCheck));
        this.activelyCheckedConditions.put(id, false);
    }

    public void registerCondition(Condition condition) {
        this.registeredConditions.put(condition.getId(), condition);
    }

    public boolean exists(String id) {
        return id.startsWith("condition.biome:") ? true : this.registeredConditions.containsKey(id);
    }

    public boolean check(String id) {
        return id.startsWith("condition.biome:")
            ? this.mc.level != null
                && this.mc.player != null
                && this.mc
                    .level
                    .registryAccess()
                    .registryOrThrow(Registries.BIOME)
                    .getKey((Biome)this.mc.level.getBiome(this.mc.player.blockPosition()).value())
                    .equals(ResourceLocation.parse(id.substring(16)))
            : this.registeredConditions.get(id).checkCondition();
    }

    public static Component getDisplayName(String id) {
        return !id.startsWith("condition.biome:") && !id.startsWith("category.biome:")
            ? Component.translatable(id)
            : Component.literal(GeneralUtils.getBiomeName(ResourceLocation.parse(id.substring(17))));
    }

    public static HashMap<ResourceLocation, ArrayList<CategorySet>> getDefaultCategories() {
        PlaylistManager playlistManager = PlaylistClient.instance.playlistManager;
        HashMap<ResourceLocation, ArrayList<CategorySet>> set = new HashMap<>();

        for (ResourceLocation songId : playlistManager.getRegisteredSongs()) {
            SongObj song = playlistManager.getRegisteredSongObj(songId);
            ArrayList<CategorySet> deepCopy = new ArrayList<>();

            for (CategorySet catSet : song.getCategories()) {
                ArrayList<String> deepCopyConditions = new ArrayList<>(catSet.getSubcategories());
                CategorySet deepCopyCatSet = new CategorySet(catSet.getCategoryId(), deepCopyConditions);
                deepCopy.add(deepCopyCatSet);
            }

            set.put(songId, deepCopy);
        }

        return set;
    }

    public static String parse(String condition) {
        return condition.contains(":") ? condition.substring(0, condition.indexOf(":")) : condition;
    }

    public String getCurrentCategory() {
        for (String categoryId : this.registeredConditions.keySet()) {
            Condition category = this.registeredConditions.get(categoryId);
            if (category.isCategory() && category.checkCondition()) {
                return categoryId;
            }
        }

        return null;
    }

    public String checkActiveConditions() {
        for (String id : this.activelyCheckedConditions.keySet()) {
            if (this.registeredConditions.get(id).checkCondition() && !this.activelyCheckedConditions.get(id)) {
                this.activelyCheckedConditions.put(id, true);
                return id;
            }

            if (!this.registeredConditions.get(id).checkCondition() && this.activelyCheckedConditions.get(id)) {
                String songCategory = this.stateManager.getStateString("currentSongCategory");
                String songCondition = this.stateManager.getStateString("currentSongCondition");
                if (songCategory != null && songCategory.equals(id) || songCondition != null && songCondition.equals(id)) {
                    PlaylistClient.instance.musicManager.stopMusic();
                    PlaylistClient.instance.musicManager.playMusic();
                }

                this.activelyCheckedConditions.put(id, false);
            }
        }

        return null;
    }

    public LinkedHashMap<String, Condition> getRegisteredConditions() {
        return this.registeredConditions;
    }

    public Map<String, Boolean> getActivelyCheckedConditions() {
        return this.activelyCheckedConditions;
    }
}
