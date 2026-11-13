package com.fudgeu.playlist.client;

import com.fudgeu.playlist.ConditionManager;
import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.gui.PlaylistScreen;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.PlaylistObj;
import com.fudgeu.playlist.ui.widgets.SongWidgetHandler;
import com.fudgeu.playlist.ui.widgets.WidgetHandler;
import com.fudgeu.playlist.utils.PlayMode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.FileToIdConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = "playlist", dist = Dist.CLIENT)
public class PlaylistClient {
    public static PlaylistClient instance;
    public static String modVersion = "v1.3.0";
    public static final Logger LOGGER = LogManager.getLogger();
    public FluxInstance fluxInstance;
    public PlaylistManager playlistManager;
    public ConditionManager conditionManager;
    public MusicManager musicManager;
    public TextureManager textureManager;
    public VolumeBarThemeManager volumeBarThemeManager;
    public StateManager stateManager;
    public ToastManager toastManager;
    public PlayMode playMode;
    public static BoolProp isLoadComplete = new BoolProp(false);
    public static Path gameDirectory;
    public PlaylistScreen playlistScreenInstance;

    public PlaylistClient(IEventBus modBus) {
        instance = this;
        gameDirectory = FMLPaths.GAMEDIR.get();
        if (!Files.exists(Path.of(gameDirectory + "/playlist/"))) {
            try {
                Files.createDirectory(Path.of(gameDirectory + "/playlist/"));
            } catch (IOException var3) {
                LOGGER.warn("Failed to create mod directory, stacktrace follows");
                LOGGER.warn(var3);
            }
        }

        if (!Files.exists(Path.of(gameDirectory + "/playlist/playlists"))) {
            try {
                Files.createDirectory(Path.of(gameDirectory + "/playlist/playlists"));
            } catch (IOException var2) {
                LOGGER.warn("Failed to create mod directory, stacktrace follows");
                LOGGER.warn(var2);
            }
        }

        this.playlistManager = new PlaylistManager();
        this.stateManager = new StateManager();
        this.playMode = new PlayMode();
        modBus.addListener((RegisterClientReloadListenersEvent event) -> event.registerReloadListener(
                new ResourceManagerReloadListener() {
                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        PlaylistClient.this.toastManager = new ToastManager();
                        PlaylistClient.this.conditionManager = new ConditionManager();
                        PlaylistClient.this.musicManager = new MusicManager();
                        PlaylistClient.this.textureManager = new TextureManager();
                        PlaylistClient.this.volumeBarThemeManager = new VolumeBarThemeManager();
                        Initializers.initConditions(PlaylistClient.this.conditionManager);
                        PlaylistClient.this.textureManager.addTexture("condition.biome", "conditions/condition.biome");
                        PlaylistClient.this.textureManager.addTexture("condition.creative", "conditions/condition.creative");
                        PlaylistClient.this.textureManager.addTexture("condition.aquatic", "conditions/condition.aquatic");
                        PlaylistClient.this.textureManager.addTexture("condition.end_dragon", "conditions/condition.end_dragon");
                        PlaylistClient.this.textureManager.addTexture("settingsGear", "gear");
                        PlaylistClient.this.textureManager.addTexture("reset", "reset");
                        PlaylistClient.this.textureManager.addTexture("checkmark", "checkmark");
                        PlaylistClient.this.textureManager.addTexture("add", "add");
                        PlaylistClient.this.textureManager.addTexture("music_note", "music_note");
                        PlaylistClient.this.textureManager.addTexture("selectionTool", "selection_tool");
                        PlaylistClient.this.textureManager.addTexture("edit", "edit");
                        PlaylistClient.this.textureManager.addTexture("trash", "trash");
                        PlaylistClient.this.textureManager.addTexture("trashCondition", "trash_condition");
                        PlaylistClient.this.textureManager.addTexture("repeat", "repeat");
                        PlaylistClient.this.textureManager.addTexture("repeatOnce", "repeat_once");
                        PlaylistClient.this.textureManager.addTexture("categoryBased", "category_based");
                        PlaylistClient.this.textureManager.addTexture("audio", "audio");
                        PlaylistClient.this.textureManager.addTexture("mute", "mute");
                        PlaylistClient.this.textureManager.addTexture("arrow_down", "arrow_down");
                        PlaylistClient.this.textureManager.addTexture("arrow_down_short", "arrow_down_short");
                        PlaylistClient.this.textureManager.addTexture("arrow_up", "arrow_up");
                        PlaylistClient.this.textureManager.addTexture("arrow_up_short", "arrow_up_short");
                        PlaylistClient.this.textureManager.addTexture("info", "info");
                        PlaylistClient.this.textureManager.addTexture("play", "play");
                        PlaylistClient.this.textureManager.addTexture("queue", "queue");
                        PlaylistClient.this.textureManager.addTexture("rewind", "rewind");
                        PlaylistClient.this.textureManager.addTexture("search", "search");
                        PlaylistClient.this.textureManager.addTexture("skip", "skip");
                        PlaylistClient.this.textureManager.addTexture("stop", "stop");
                        PlaylistClient.this.textureManager.addTexture("default", "default");
                        PlaylistClient.this.playlistManager = new PlaylistManager();
                        FileToIdConverter songInfoFinder = FileToIdConverter.json("songinfo");

                        for (Resource id : songInfoFinder.listMatchingResources(manager).values()) {
                            try (InputStream stream = id.open()) {
                                JsonObject songInfoJson = JsonParser.parseReader(new BufferedReader(new InputStreamReader(stream))).getAsJsonObject();

                                for (String songId : songInfoJson.keySet()) {
                                    JsonObject song = songInfoJson.getAsJsonObject(songId);
                                    JsonElement altName = song.get("alt");
                                    JsonElement type = song.get("type");
                                    JsonElement weight = song.get("weight");
                                    JsonObject categoriesObject = song.get("categories").getAsJsonObject();
                                    ArrayList<CategorySet> categories = new ArrayList<>();

                                    for (String i : categoriesObject.keySet()) {
                                        JsonArray conditionsArray = categoriesObject.get(i).getAsJsonArray();
                                        ArrayList<String> conditions = new ArrayList<>();

                                        for (JsonElement j : conditionsArray) {
                                            conditions.add(j.getAsString());
                                        }

                                        categories.add(new CategorySet(i, conditions));
                                    }

                                    PlaylistClient.this.playlistManager
                                        .registerSong(
                                            songId,
                                            song.get("title").getAsString(),
                                            song.get("artist").getAsString(),
                                            song.get("album").getAsString(),
                                            altName != null ? altName.getAsString() : null,
                                            type != null ? type.getAsString() : null,
                                            weight != null ? weight.getAsInt() : 1,
                                            categories
                                        );
                                    JsonElement track = song.get("track");
                                    if (track != null) {
                                        PlaylistClient.this.playlistManager
                                            .getRegisteredSongObj(ResourceLocation.fromNamespaceAndPath("playlist", songId))
                                            .setTrackNumber(track.getAsInt());
                                    }
                                }
                            } catch (Exception var27) {
                                PlaylistClient.LOGGER.error("Failed to load song resource info, stacktrace follows");
                                PlaylistClient.LOGGER.error(var27);
                            }
                        }

                        File dir = new File(PlaylistClient.gameDirectory.toString() + "/playlist/playlists/");
                        File[] directoryListing = dir.listFiles();
                        if (directoryListing != null) {
                            for (File child : directoryListing) {
                                try {
                                    JsonObject playlistJson = JsonParser.parseReader(new BufferedReader(new InputStreamReader(new FileInputStream(child))))
                                        .getAsJsonObject();
                                    PlaylistObj playlist = new PlaylistObj(playlistJson.get("title").getAsString());
                                    playlist.setUniqueId(playlistJson.get("uuid").getAsString());
                                    playlist.setTimeBetweenSongs(playlistJson.get("options").getAsJsonObject().get("timeBetweenSongs").getAsInt());
                                    playlist.setTimeVariance(playlistJson.get("options").getAsJsonObject().get("timeVariance").getAsInt());

                                    for (JsonElement id : playlistJson.get("contents").getAsJsonArray()) {
                                        playlist.loadSong(ResourceLocation.parse(id.getAsString()));
                                    }

                                    JsonObject customizations = playlistJson.get("customizations").getAsJsonObject();
                                    JsonObject categorySets = customizations.get("categorySets").getAsJsonObject();
                                    JsonObject weights = customizations.get("weights").getAsJsonObject();
                                    HashMap<ResourceLocation, ArrayList<CategorySet>> parsedCategorySets = new HashMap<>();
                                    HashMap<ResourceLocation, Integer> parsedWeights = new HashMap<>();

                                    for (String song : categorySets.keySet()) {
                                        ArrayList<CategorySet> parsedSets = new ArrayList<>();

                                        for (JsonElement set : categorySets.get(song).getAsJsonArray()) {
                                            JsonObject setObject = set.getAsJsonObject();
                                            parsedSets.add(CategorySet.deserialize(setObject));
                                        }

                                        parsedCategorySets.put(ResourceLocation.parse(song), parsedSets);
                                    }

                                    playlist.setCustomCategories(parsedCategorySets);

                                    for (String key : weights.keySet()) {
                                        parsedWeights.put(ResourceLocation.parse(key), weights.get(key).getAsInt());
                                    }

                                    playlist.setCustomWeights(parsedWeights);
                                    PlaylistClient.this.playlistManager.registerPlaylist(playlist);
                                } catch (FileNotFoundException var25) {
                                    PlaylistClient.LOGGER.warn("Playlists failed to load, falling back to default playlist; stacktrace follows");
                                    PlaylistClient.LOGGER.warn(var25);
                                }
                            }
                        }

                        File orderFile = new File(PlaylistClient.gameDirectory.toString() + "/playlist/playlist_order.json");

                        try {
                            ArrayList<String> UUIDs = new ArrayList<>();
                            JsonObject json = JsonParser.parseReader(new BufferedReader(new InputStreamReader(new FileInputStream(orderFile))))
                                .getAsJsonObject();

                            for (JsonElement element : json.get("order").getAsJsonArray()) {
                                UUIDs.add(element.getAsString());
                            }

                            PlaylistClient.this.playlistManager.organizeBasedOn(UUIDs);
                            JsonElement defaultPlaylist = json.get("default");
                            if (defaultPlaylist != null) {
                                PlaylistClient.this.playlistManager.setDefaultPlaylist(defaultPlaylist.getAsString());
                            } else {
                                PlaylistClient.LOGGER.info("No default playlist found in playlist info file, setting to 0");
                                PlaylistClient.this.playlistManager.setDefaultPlaylist(0);
                            }
                        } catch (Exception var24) {
                            PlaylistClient.LOGGER.warn("Playlist info file corrupted, deleting. Will be regenerated on the next save.");
                            orderFile.delete();
                        }

                        if (PlaylistClient.this.playlistManager.getRegisteredPlaylists().size() == 0) {
                            PlaylistObj defaultPlaylist = new PlaylistObj("My Playlist");

                            for (ResourceLocation songId : PlaylistClient.instance.playlistManager.getRegisteredSongs()) {
                                defaultPlaylist.addSong(songId);
                            }

                            PlaylistClient.this.playlistManager.registerPlaylist(defaultPlaylist);
                            PlaylistClient.this.playlistManager.setDefaultPlaylist(0);
                        }

                        PlaylistClient.this.addNewSongsToDefaultPlaylist();
                        PlaylistClient.this.playlistManager.finishInit();
                        PlaylistClient.this.musicManager.setCurrentPlaylist(PlaylistClient.this.playlistManager.getSafeDefaultPlaylist());
                        PlaylistClient.this.fluxInstance = Initializers.initFlux(manager);
                        PlaylistClient.isLoadComplete.set(true);
                    }
                }
            ));
        ConfigManager.init();
        ConfigManager.loadConfig(this.stateManager);
        WidgetHandler.init();
        SongWidgetHandler.init();
        KeyBindHandler.init();
        PlaylistTickHandler.init();
        modBus.addListener((RegisterKeyMappingsEvent event) -> KeyBindHandler.INSTANCE.register(event));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> PlaylistTickHandler.INSTANCE.handleGameTick(isLoadComplete));
        // Replaces the Fabric GameRenderer mixin that drew the HUD widgets right before the in-game HUD.
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Pre event) ->
            PlaylistTickHandler.INSTANCE.handleInGameRenderTick(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false)));
    }

    /**
     * Port addition: songs that did not exist the last time the game ran (e.g. tracks added in a
     * Minecraft update) are appended to the default playlist. Songs the player has seen before are
     * tracked in playlist/known_songs.json, so songs they deliberately removed are not re-added.
     */
    private void addNewSongsToDefaultPlaylist() {
        Path knownFile = Path.of(gameDirectory + "/playlist/known_songs.json");
        java.util.Set<ResourceLocation> known = new java.util.HashSet<>();
        if (Files.exists(knownFile)) {
            try (BufferedReader reader = Files.newBufferedReader(knownFile)) {
                for (JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
                    ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
                    if (id != null) {
                        known.add(id);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to read known songs file, rebuilding it", e);
            }
        } else {
            // First run with this file (fresh install or upgrade): treat songs already in any playlist as known.
            for (int id : this.playlistManager.getRegisteredPlaylists()) {
                known.addAll(this.playlistManager.getRegisteredPlaylist(id).getPlaylistItems());
            }
        }

        int defaultId = this.playlistManager.getSafeDefaultPlaylist();
        if (defaultId != -1) {
            PlaylistObj defaultPlaylist = this.playlistManager.getRegisteredPlaylist(defaultId);
            int added = 0;
            for (ResourceLocation songId : this.playlistManager.getRegisteredSongs()) {
                if (!known.contains(songId) && !defaultPlaylist.getPlaylistItems().contains(songId)) {
                    defaultPlaylist.addSong(songId);
                    added++;
                }
            }
            if (added > 0) {
                LOGGER.info("Added {} new song(s) to default playlist '{}'", added, defaultPlaylist.getTitle());
                this.playlistManager.saveAll();
            }
        }

        JsonArray all = new JsonArray();
        for (ResourceLocation songId : this.playlistManager.getRegisteredSongs()) {
            all.add(songId.toString());
        }
        try {
            Files.writeString(knownFile, all.toString());
        } catch (IOException e) {
            LOGGER.warn("Failed to write known songs file", e);
        }
    }
}
