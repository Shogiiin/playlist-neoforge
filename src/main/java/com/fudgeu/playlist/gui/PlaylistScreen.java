package com.fudgeu.playlist.gui;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.enums.SortCategory;
import com.fudgeu.playlist.enums.SortDirection;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxUI;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.gui.screens.addtoplaylist.AddToPlaylistScreen;
import com.fudgeu.playlist.gui.widgets.AlbumBrowser;
import com.fudgeu.playlist.gui.widgets.Box;
import com.fudgeu.playlist.gui.widgets.ContextMenu;
import com.fudgeu.playlist.gui.widgets.ExpandedVolumeControl;
import com.fudgeu.playlist.gui.widgets.TabSelector;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.gui.widgets.TransparentTextField;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.SongObj;
import com.fudgeu.playlist.objects.SortQuery;
import com.fudgeu.playlist.utils.Mode;
import com.fudgeu.playlist.utils.PlayMode;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.vertex.PoseStack;

public class PlaylistScreen extends Screen {
    private RGBA accent;
    private TransparentTextField searchField;
    protected TransparentButton playButton;
    private TransparentButton selectionToolButton;
    private TransparentButton closeButton;
    private SortButton titleSortButton;
    private SortButton albumSortButton;
    private SongListUI songListUI;
    private TabSelector tabSelector;
    private int selectionToolStartX;
    private int selectionToolStartY;
    private int selectionToolEndX;
    private int selectionToolEndY;
    private TransparentButton closeSelectionTool;
    private TransparentButton deleteSelected;
    private TransparentButton addSelected;
    private final ArrayList<ResourceLocation> selectedSongs = new ArrayList<>();
    protected TransparentButton allSongsButton;
    private PlaylistSelector playlistSelector;
    private TransparentButton playPauseButton;
    private TransparentButton repeatButton;
    private TransparentButton volumeControl;
    private ExpandedVolumeControl expandedVolumeControl;
    public SortQuery sortQuery = new SortQuery(SortCategory.TITLE, SortDirection.ASCENDING);
    private int currentPlaylist = -1;
    public Map<ResourceLocation, Integer> songMap = new LinkedHashMap<>();
    private final Minecraft mc;
    private final MusicManager musicManager;
    private final StateManager stateManager;
    private final ToastManager toastManager;
    private final TextureManager textureManager;
    private PlaylistManager playlistManager;
    private final PlayMode playMode;
    protected static int screenWidth;
    protected static int screenHeight;
    private boolean isContextMenuActive = false;
    private boolean freshScreen = false;
    private boolean selectionMode = false;
    // Port addition: album browser shown in the content area instead of the song list.
    private boolean albumMode = false;
    private AlbumBrowser albumBrowser;

    public PlaylistScreen() {
        super(Component.literal("Playlist"));
        this.mc = Minecraft.getInstance();
        this.musicManager = PlaylistClient.instance.musicManager;
        this.stateManager = PlaylistClient.instance.stateManager;
        this.textureManager = PlaylistClient.instance.textureManager;
        this.toastManager = PlaylistClient.instance.toastManager;
        this.playMode = PlaylistClient.instance.playMode;
        if (this.mc.screen == null) {
            this.freshScreen = true;
            this.stateManager.clearStateString("currentCategoryTab");
        }
    }

    protected void init() {
        assert this.minecraft != null;

        this.clearWidgets();
        screenWidth = this.width;
        screenHeight = this.height;
        this.accent = RGBA.hex(this.stateManager.getStateInt("accentHex"));
        this.playlistManager = PlaylistClient.instance.playlistManager;
        this.currentPlaylist = this.getStateInt("currentlyViewedPlaylist", this.musicManager.getCurrentPlaylist());
        if (!PlaylistClient.instance.playlistManager.getRegisteredPlaylists().contains(this.currentPlaylist) && this.currentPlaylist != -1) {
            this.currentPlaylist = this.playlistManager.getDefaultPlaylist();
        }

        if (!PlaylistClient.instance.playlistManager.getRegisteredPlaylists().contains(this.currentPlaylist) && this.currentPlaylist != -1) {
            this.currentPlaylist = -1;
        }

        LinkedHashMap<String, String> tabs = new LinkedHashMap<>();
        tabs.put("all", "All");
        tabs.put("category.overworld", "Overworld");
        tabs.put("category.nether", "Nether");
        tabs.put("category.the_end", "End");
        tabs.put("category.menu", "Menu");
        String savedCategory = this.stateManager.getStateString("currentCategoryTab");
        String currentCategory;
        if (savedCategory == null) {
            currentCategory = (currentCategory = PlaylistClient.instance.conditionManager.getCurrentCategory()) != null ? currentCategory : "all";
        } else {
            currentCategory = savedCategory;
        }

        this.stateManager.setStateString("currentCategoryTab", currentCategory);
        int mainContentLeft = 153;
        this.tabSelector = (TabSelector)this.addRenderableWidget(new TabSelector(mainContentLeft, 45, screenWidth, 70, tabs, currentCategory));
        this.prepareInitialSongList();
        TransparentButton settingsIcon = (TransparentButton)this.addRenderableWidget(new TransparentButton(112, 20, 12, 12, null, onPress -> {
            FluxInstance fluxInstance = PlaylistClient.instance.fluxInstance;
            PropProvider props = new PropProvider();
            props.setRunnable("onExit", () -> this.minecraft.setScreen(this));
            fluxInstance.buildComponent("PlaylistApp", "PlaylistApp", props).ifPresent(settingsMenu -> {
                fluxInstance.setElementAsRoot(settingsMenu);
                this.setScreen(FluxUI.createScreenFromElement(fluxInstance.getRootElement()));
            });
        }));
        settingsIcon.setIcon(this.textureManager.getTexture("settingsGear"));
        settingsIcon.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        settingsIcon.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        int searchFieldLength = Math.max(75, Math.min(150, this.width - 160 - 30 - 5 - 20 - 5 - this.getHeaderLength() - 50 - 20));
        this.searchField = (TransparentTextField)this.addRenderableWidget(
            new TransparentTextField(
                this.font, this.width - searchFieldLength - 30 - 5 - 20 - 5, 15, searchFieldLength, 20, Component.translatable("generic.search")
            )
        );
        this.searchField.setIcon(this.textureManager.getTexture("search"));
        this.searchField.setValue(this.getStateString("searchQuery", null));
        this.closeButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(this.width - 30, 15, 20, 20, Component.literal("x"), onPress -> this.setScreen(null))
        );
        this.allSongsButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(0, 50, 130, 30, Component.translatable("screen.playlist.all_songs"), onPress -> {
                this.allSongsButton.overrideFocusedStatus(true);
                this.setSelectedPlaylist(-1);
            })
        );
        this.allSongsButton.setDefaultColor(new RGBA(0.05F));
        this.allSongsButton.setHoveredColor(new RGBA(0.075F));
        this.allSongsButton.setActiveColor(new RGBA(0.1F));
        this.allSongsButton.setTextAlignment(TransparentButton.TextAlignment.LEFT);
        this.allSongsButton.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
        this.allSongsButton.setIcon(this.textureManager.getTexture("music_note"));
        if (this.currentPlaylist == -1 && !this.albumMode) {
            this.allSongsButton.overrideFocusedStatus(true);
        }

        TransparentButton createPlaylistButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(0, 80, 130, 30, Component.translatable("screen.playlist.new_playlist"), onPress -> {
                PropProvider props = new PropProvider();
                props.setRunnable("onExit", () -> this.minecraft.setScreen(this));
                FluxInstance fluxInstance = FluxInstance.getInstance();
                fluxInstance.buildComponent("CreatePlaylistApp", "CreatePlaylistApp", props)
                    .ifPresent(app -> this.setScreen(FluxUI.createScreenFromElement(app)));
            })
        );
        createPlaylistButton.setDefaultColor(new RGBA(0.05F));
        createPlaylistButton.setHoveredColor(new RGBA(0.075F));
        createPlaylistButton.setTextAlignment(TransparentButton.TextAlignment.LEFT);
        createPlaylistButton.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
        createPlaylistButton.setIcon(this.textureManager.getTexture("add"));
        // Port addition: album browser
        TransparentButton albumsButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(0, 110, 130, 30, Component.translatable("screen.playlist.albums"), onPress -> this.showAlbums())
        );
        albumsButton.setDefaultColor(new RGBA(0.05F));
        albumsButton.setHoveredColor(new RGBA(0.075F));
        albumsButton.setActiveColor(new RGBA(0.1F));
        albumsButton.overrideFocusedStatus(this.albumMode);
        albumsButton.setTextAlignment(TransparentButton.TextAlignment.LEFT);
        albumsButton.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
        albumsButton.setIcon(this.textureManager.getTexture("queue"));
        this.playButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(
                this.width - searchFieldLength - 30 - 5 - 20 - 5 - 5 - 75, 15, 75, 20, Component.translatable("generic.play"), onPress -> {
                    if (PlaylistClient.instance.musicManager.setCurrentPlaylist(this.currentPlaylist)) {
                        this.playButton.setDefaultColor(this.accent);
                        this.playButton.setHoveredColor(this.accent);
                        this.playButton.setMessage(Component.translatable("generic.playing"));
                        PlaylistClient.instance.musicManager.skipWithInterlude();
                    } else {
                        this.toastManager.addToast(Component.translatable("toast.cannot_play_empty"), 60);
                    }
                }
            )
        );
        this.playButton.setIcon(this.textureManager.getTexture("play"));
        this.playButton.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
        this.updatePlayButtonState();
        this.titleSortButton = (SortButton)this.addRenderableWidget(
            new SortButton(this, 201, 75, Component.translatable("generic.title").withStyle(ChatFormatting.ITALIC), SortCategory.TITLE, onPress -> {
                if (this.sortQuery.getCategory() != SortCategory.TITLE && this.sortQuery.getCategory() != SortCategory.ARTIST) {
                    this.sortQuery.setSortCategory(SortCategory.TITLE);
                    this.sortQuery.setSortDirection(SortDirection.ASCENDING);
                    this.titleSortButton.updateMessage();
                } else if (this.sortQuery.getCategory() == SortCategory.TITLE && this.sortQuery.getDirection() == SortDirection.ASCENDING) {
                    this.sortQuery.reverseSortDirection();
                } else if (this.sortQuery.getCategory() == SortCategory.TITLE && this.sortQuery.getDirection() == SortDirection.DESCENDING) {
                    this.sortQuery.setSortCategory(SortCategory.ARTIST);
                    this.sortQuery.reverseSortDirection();
                    this.titleSortButton.updateMessage();
                } else if (this.sortQuery.getCategory() == SortCategory.ARTIST && this.sortQuery.getDirection() == SortDirection.ASCENDING) {
                    this.sortQuery.reverseSortDirection();
                } else if (this.sortQuery.getCategory() == SortCategory.ARTIST && this.sortQuery.getDirection() == SortDirection.DESCENDING) {
                    this.sortQuery.setSortCategory(SortCategory.TITLE);
                    this.sortQuery.reverseSortDirection();
                    this.titleSortButton.updateMessage();
                }

                this.refreshSongList();
            })
        );
        this.albumSortButton = (SortButton)this.addRenderableWidget(
            new SortButton(this, 371, 75, Component.translatable("generic.album").withStyle(ChatFormatting.ITALIC), SortCategory.ALBUM, onPress -> {
                if (this.sortQuery.getCategory() != SortCategory.ALBUM) {
                    this.sortQuery.setSortCategory(SortCategory.ALBUM);
                    this.sortQuery.setSortDirection(SortDirection.ASCENDING);
                } else {
                    this.sortQuery.reverseSortDirection();
                }

                this.titleSortButton.updateMessage();
                this.refreshSongList();
            })
        );
        this.songListUI = (SongListUI)this.addRenderableWidget(
            new SongListUI(this, Minecraft.getInstance(), this.width - 140, this.height - 83, 83, this.height, 40, this.width)
        );
        this.songListUI.renderBottomOffScreen(true);
        this.songListUI.setLeftPos(140);
        this.songListUI.setHoveredColor(new RGBA(0.0F, 0.5F));
        this.songListUI.setSelectedColor(new RGBA(0.0F, 0.7F));
        this.songListUI.setScrollAmount(this.getStateInt("songListScrollValue", 0));
        this.playlistSelector = (PlaylistSelector)this.addRenderableWidget(
            new PlaylistSelector(this, Minecraft.getInstance(), 130, this.height - 150 - 86, 150, this.height - 86, 30)
        );
        this.playlistSelector.renderBottomOffScreen(false);
        this.playlistSelector.setLeftPos(0);
        this.addRenderableOnly(new Box(0, this.height - 45, 130, this.height, new RGBA(0.035F)));
        TransparentButton previousButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(0, this.height - 45, 43, 20, null, onPress -> PlaylistClient.instance.musicManager.previous())
        );
        previousButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        previousButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        previousButton.setIcon(this.textureManager.getTexture("rewind"));
        this.playPauseButton = (TransparentButton)this.addRenderableWidget(new TransparentButton(43, this.height - 45, 44, 20, null, onPress -> {
            if (PlaylistClient.instance.musicManager.isMusicStopped()) {
                PlaylistClient.instance.musicManager.playMusic();
                this.playPauseButton.setIcon(this.textureManager.getTexture("stop"));
            } else {
                PlaylistClient.instance.musicManager.stopMusic();
                this.playPauseButton.setIcon(this.textureManager.getTexture("play"));
            }
        }));
        this.playPauseButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        this.playPauseButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        if (PlaylistClient.instance.musicManager.isMusicStopped()) {
            this.playPauseButton.setIcon(this.textureManager.getTexture("play"));
        } else {
            this.playPauseButton.setIcon(this.textureManager.getTexture("stop"));
        }

        TransparentButton skipButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(87, this.height - 45, 43, 20, null, onPress -> PlaylistClient.instance.musicManager.skip())
        );
        skipButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        skipButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        skipButton.setIcon(this.textureManager.getTexture("skip"));
        this.repeatButton = (TransparentButton)this.addRenderableWidget(new TransparentButton(22, this.height - 25, 43, 20, null, onPress -> {
            switch (this.playMode.getPlayMode()) {
                case CATEGORY_BASED:
                    this.playMode.set(Mode.REPEAT);
                    break;
                case REPEAT:
                    this.playMode.set(Mode.REPEAT_ONCE);
                    break;
                case REPEAT_ONCE:
                    this.playMode.set(Mode.CATEGORY_BASED);
            }
        }));
        this.repeatButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        this.repeatButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        this.updatePlayMode(this.playMode.getPlayMode());
        this.expandedVolumeControl = (ExpandedVolumeControl)this.addRenderableWidget(new ExpandedVolumeControl(76, this.height - 25));
        this.volumeControl = (TransparentButton)this.addRenderableWidget(new TransparentButton(65, this.height - 25, 43, 20, null, onPress -> {
            if (this.musicManager.toggleMute()) {
                this.volumeControl.setIcon(this.textureManager.getTexture("mute"));
            } else {
                this.volumeControl.setIcon(this.textureManager.getTexture("audio"));
            }
        }));
        this.volumeControl.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        this.volumeControl.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        this.volumeControl.setIcon(this.textureManager.getTexture("audio"));
        this.volumeControl.setHoverAction(onHover -> {
            this.expandedVolumeControl.setVisible(true);
            onHover.forceHover = true;
        });
        this.expandedVolumeControl.setParentButton(this.volumeControl);
        this.selectionToolStartX = this.width - 205;
        this.selectionToolStartY = this.height - 40;
        this.selectionToolEndX = this.width - 10;
        this.selectionToolEndY = this.height - 10;
        this.selectionToolButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(this.width - 55, 15, 20, 20, null, onPress -> this.toggleSelectionMode())
        );
        this.selectionToolButton.setIcon(this.textureManager.getTexture("selectionTool"));
        this.selectionToolButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        this.closeSelectionTool = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(this.width - 35, this.height - 35, 20, 20, Component.literal("x"), onPress -> this.toggleSelectionMode())
        );
        this.closeSelectionTool.visible = this.selectionMode;
        this.deleteSelected = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(this.width - 60, this.height - 35, 20, 20, null, onPress -> {
                for (ResourceLocation id : this.selectedSongs) {
                    this.playlistManager.getRegisteredPlaylist(this.currentPlaylist).removeSong(id);
                }

                this.toastManager.addToast(Component.translatable("toast.songs_removed", new Object[]{this.selectedSongs.size()}), 60);
                this.selectedSongs.clear();
                this.refreshSongList();
            })
        );
        this.deleteSelected.setIcon(this.textureManager.getTexture("trash"));
        this.deleteSelected.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        this.deleteSelected.disabled = this.currentPlaylist == -1;
        this.deleteSelected.visible = this.selectionMode;
        this.addSelected = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(
                this.width - 85,
                this.height - 35,
                20,
                20,
                null,
                onPress -> PlaylistClient.instance
                    .playlistScreenInstance
                    .setScreen(new AddToPlaylistScreen(this.selectedSongs, this.currentPlaylist, this.mc.screen))
            )
        );
        this.addSelected.setIcon(this.textureManager.getTexture("add"));
        this.addSelected.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        this.addSelected.visible = this.selectionMode;
        this.processSearch();
        if (this.albumMode) {
            // Swap the song list area for the album browser; the sidebar stays as is.
            this.removeWidget(this.tabSelector);
            this.removeWidget(this.searchField);
            this.removeWidget(this.playButton);
            this.removeWidget(this.titleSortButton);
            this.removeWidget(this.albumSortButton);
            this.removeWidget(this.songListUI);
            this.removeWidget(this.selectionToolButton);
            this.removeWidget(this.closeSelectionTool);
            this.removeWidget(this.deleteSelected);
            this.removeWidget(this.addSelected);
            if (this.albumBrowser == null) {
                this.albumBrowser = new AlbumBrowser(this::rebuildKeepingState);
            }
            this.albumBrowser.setBounds(140, this.width - 140, this.height);
            this.addRenderableWidget(this.albumBrowser);
            for (TransparentButton button : this.albumBrowser.createButtons()) {
                this.addRenderableWidget(button);
            }
        }

        if (this.freshScreen) {
            this.freshScreen = false;
        }

        super.init();
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Background is drawn explicitly at the start of render(); Screen.render() would otherwise draw it (and blur) twice.
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        if (this.stateManager.getStateBool("darkenBackground")) {
            RenderUtils.drawRect(0, 0, this.width, this.height, 0.0F, 0.0F, 0.0F, 0.65F);
        }

        RenderUtils.drawRect(0, 0, 130, this.height, 0.05F, 0.05F, 0.05F, 1.0F);
        RenderUtils.drawRect(5, 147, 125, 148, 0.24F, 0.24F, 0.24F, 1.0F);
        PoseStack matrices = context.pose();
        matrices.pushPose();
        float textScale = 1.5F;
        float positionScale = 1.0F / textScale;
        matrices.scale(textScale, textScale, textScale);
        context.drawString(this.mc.font, "Playlist", (int)(10.0F * positionScale), (int)(20.0F * positionScale), 16777215, false);
        matrices.popPose();
        Component playlistDisplayName = this.currentPlaylist == -1
            ? Component.translatable("screen.playlist.all_songs")
            : Component.literal(PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getTitle());
        int songCount = PlaylistClient.instance.playlistManager.getSongCount(this.currentPlaylist);
        if (this.albumMode) {
            // album browser draws its own header
        } else if (songCount == 1) {
            context.drawString(this.mc.font, playlistDisplayName, 160, 21, 16777215, false);
            context.drawString(
                this.mc.font,
                Component.translatable("screen.playlist.song", new Object[]{songCount}).withStyle(ChatFormatting.ITALIC),
                160 + this.mc.font.width(playlistDisplayName) + 7,
                21,
                11184810,
                false
            );
        } else {
            context.drawString(this.mc.font, playlistDisplayName, 160, 21, 16777215, false);
            context.drawString(
                this.mc.font,
                Component.translatable("screen.playlist.songs", new Object[]{songCount}).withStyle(ChatFormatting.ITALIC),
                160 + this.mc.font.width(playlistDisplayName) + 7,
                21,
                11184810,
                false
            );
        }

        SongObj currentSong = PlaylistClient.instance.playlistManager.getRegisteredSongObj(this.musicManager.getCurrentSongId());
        this.playlistSelector.updateBottom(this.height - 45);
        if (currentSong != null) {
            RenderUtils.drawRect(0, this.height - 86, 130, this.height - 40, 0.035F, 0.035F, 0.035F, 1.0F);
            context.blit(currentSong.getAlbumArt(), 10, this.height - 78, 0.0F, 0.0F, 30, 30, 30, 30);
            String songTitle = currentSong.getTitle();
            if (songTitle.length() > 14) {
                songTitle = songTitle.substring(0, 14);
                songTitle = songTitle + "...";
            }

            context.drawString(this.mc.font, songTitle, 45, this.height - 73, 16777215, false);
            context.drawString(this.mc.font, currentSong.getArtist(), 45, this.height - 62, 11184810, false);
            this.playlistSelector.updateBottom(this.height - 86);
        } else if (PlaylistClient.instance.musicManager.isInInterlude()) {
            RenderUtils.drawRect(0, this.height - 65, 130, this.height - 45, 0.035F, 0.035F, 0.035F, 1.0F);
            Component interludeText = Component.translatable("generic.interlude").withStyle(ChatFormatting.ITALIC);
            context.drawCenteredString(this.mc.font, interludeText, 65, this.height - 58, 11184810);
            int interludeTimerStartX = (130 - this.mc.font.width(interludeText)) / 2 + 5;
            int interludeTimerEndX = (130 + this.mc.font.width(interludeText)) / 2 - 5;
            int interludeTimerMidpoint = interludeTimerStartX
                + Math.round(
                    (interludeTimerEndX - interludeTimerStartX)
                        * (1.0F - (float)this.musicManager.getTimeUntilNextSong() / this.musicManager.getInitialTimerValue())
                );
            RenderUtils.drawRect(interludeTimerStartX, this.height - 47, interludeTimerMidpoint, this.height - 46, new RGBA(0.3F));
            RenderUtils.drawRect(interludeTimerMidpoint, this.height - 47, interludeTimerEndX, this.height - 46, new RGBA(0.15F));
            this.playlistSelector.updateBottom(this.height - 65);
        }

        super.render(context, mouseX, mouseY, delta);
        if (PlaylistClient.instance.musicManager.getCurrentPlaylist() == -1) {
            RenderUtils.drawRect(0, 55, 1, 75, this.accent.r, this.accent.g, this.accent.b, 1.0F);
        }

        if (this.selectionMode && !this.albumMode) {
            RenderUtils.drawRect(this.selectionToolStartX, this.selectionToolStartY, this.selectionToolEndX, this.selectionToolEndY, 0.05F, 0.05F, 0.05F, 1.0F);
            String translationKey = this.selectedSongs.size() == 1 ? "screen.playlist.song_selected" : "screen.playlist.songs_selected";
            Component selectionToolText = Component.translatable(translationKey, new Object[]{this.selectedSongs.size()});
            context.drawString(this.mc.font, selectionToolText, this.width - 195, this.height - 29, 16777215, false);
            this.closeSelectionTool.render(context, mouseX, mouseY, delta);
            this.addSelected.render(context, mouseX, mouseY, delta);
            this.deleteSelected.render(context, mouseX, mouseY, delta);
        }

        this.toastManager.render(context, this.width, this.height);
    }

    public void tick() {
        this.searchField.tick();
        this.stateManager.setStateString("currentCategoryTab", this.tabSelector.getSelectedTab());
        if (PlaylistClient.instance.musicManager.isMusicStopped()) {
            this.playPauseButton.setIcon(this.textureManager.getTexture("play"));
        } else {
            this.playPauseButton.setIcon(this.textureManager.getTexture("stop"));
        }

        this.addSelected.disabled = this.selectedSongs.size() == 0;
        if (this.mc.isPaused()) {
            this.mc.getSoundManager().tick(false);
        }

        this.updatePlayButtonState();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = this.children().size() - 1; i >= 0; i--) {
            GuiEventListener element = (GuiEventListener)this.children().get(i);
            if (this.isContextMenuActive) {
                if (element instanceof ContextMenu) {
                    if (!element.mouseClicked(mouseX, mouseY, button)) {
                        this.isContextMenuActive = false;
                        ((ContextMenu)element).close();
                        this.removeWidget(element);
                    }
                    break;
                }
            } else if (element.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(element);
                if (button == 0) {
                    this.setDragging(true);
                }

                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = this.children().size() - 1; i >= 0; i--) {
            GuiEventListener element = (GuiEventListener)this.children().get(i);
            element.mouseReleased(mouseX, mouseY, button);
        }

        return true;
    }

    public boolean keyPressed(int arg1, int arg2, int arg3) {
        if (arg1 == 256 && this.albumMode && this.albumBrowser != null && this.albumBrowser.isAlbumOpen()) {
            this.albumBrowser.showGrid();
            return true;
        }

        boolean ret = super.keyPressed(arg1, arg2, arg3);
        if (this.searchField.isFocused()) {
            this.processSearch();
        }

        return ret;
    }

    public boolean charTyped(char typedChar, int keyCode) {
        boolean ret = super.charTyped(typedChar, keyCode);
        if (this.searchField.isFocused()) {
            this.processSearch();
        }

        return ret;
    }

    public void removed() {
        this.playlistManager.saveAll();
    }

    private int getHeaderLength() {
        int playlistNameLength = this.mc
            .font
            .width(
                this.currentPlaylist == -1
                    ? Component.translatable("screen.playlist.all_songs")
                    : Component.literal(PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getTitle())
            );
        int songCount = PlaylistClient.instance.playlistManager.getSongCount(this.currentPlaylist);
        int songCountLength = this.mc
            .font
            .width(
                Component.translatable(songCount == 1 ? "screen.playlist.song" : "screen.playlist.songs", new Object[]{songCount})
                    .withStyle(ChatFormatting.ITALIC)
            );
        return playlistNameLength + songCountLength + 7;
    }

    public int getSelectedPlaylist() {
        return this.currentPlaylist;
    }

    public void updatePlayButtonState() {
        if (this.currentPlaylist == PlaylistClient.instance.musicManager.getCurrentPlaylist()) {
            this.playButton.setDefaultColor(this.accent);
            this.playButton.setHoveredColor(this.accent.lighter());
            this.playButton.setMessage(Component.translatable("generic.playing"));
        } else {
            this.playButton.setStyle(TransparentButton.ButtonStyle.DEFAULT);
            this.playButton.setMessage(Component.translatable("generic.play"));
        }
    }

    public void setSelectedPlaylist(int id) {
        if (this.albumMode) {
            // Leaving the album browser: rebuild with the song list for the chosen playlist.
            this.albumMode = false;
            this.currentPlaylist = id;
            this.rebuildKeepingState();
            return;
        }

        this.currentPlaylist = id;
        this.selectedSongs.clear();
        this.deleteSelected.disabled = this.currentPlaylist == -1;
        this.refreshSongList();
        this.updatePlayButtonState();
    }

    private void prepareInitialSongList() {
        if (this.currentPlaylist == -1) {
            List<ResourceLocation> songList = new ArrayList<>();

            for (ResourceLocation id : PlaylistClient.instance.playlistManager.getRegisteredSongs()) {
                songList.add(id);
            }

            this.sortList(this.filterPlaylistItems(songList));
        } else {
            this.sortList(this.filterPlaylistItems(PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getPlaylistItems()));
        }
    }

    public void refreshSongList() {
        if (this.searchField.getValue() != null && this.searchField.getValue().length() > 0) {
            this.processSearch();
        } else if (this.currentPlaylist == -1) {
            int i = 0;
            List<ResourceLocation> songList = new ArrayList<>();

            for (ResourceLocation id : PlaylistClient.instance.playlistManager.getRegisteredSongs()) {
                songList.add(id);
                i++;
            }

            this.sortList(this.filterPlaylistItems(songList));
        } else {
            this.sortList(this.filterPlaylistItems(PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getPlaylistItems()));
        }

        this.songListUI.setScrollAmount(0.0);
        this.songListUI.refreshEntries();
    }

    private List<ResourceLocation> filterPlaylistItems(List<ResourceLocation> playlistItems) {
        String filter = this.tabSelector.getSelectedTab();
        List<ResourceLocation> filteredList = new ArrayList<>();
        if (filter.equals("all")) {
            return playlistItems;
        } else {
            for (ResourceLocation id : playlistItems) {
                ArrayList<CategorySet> categorySets;
                if (this.currentPlaylist == -1) {
                    categorySets = this.playlistManager.getRegisteredSongObj(id).getCategories();
                } else {
                    categorySets = this.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getCustomCategories().get(id);
                }

                for (CategorySet categorySet : categorySets) {
                    if (Objects.equals(categorySet.getCategoryId(), filter)) {
                        filteredList.add(id);
                        break;
                    }
                }
            }

            return filteredList;
        }
    }

    private void processSearch() {
        List<ResourceLocation> songList = new ArrayList<>();
        int i = 0;
        if (this.searchField.getValue() == null && this.searchField.getValue() == "") {
            for (ResourceLocation id : PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getPlaylistItems()) {
                songList.add(id);
                i++;
            }
        } else {
            for (ResourceLocation id : PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getPlaylistItems()) {
                SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(id);
                String searchQuery = this.searchField.getValue().toLowerCase();
                if (song != null
                    && (
                        song.getTitle().toLowerCase().contains(searchQuery)
                            || song.getArtist().toLowerCase().contains(searchQuery)
                            || song.getAlbum().toLowerCase().contains(searchQuery)
                            || song.getAltName() != null && song.getAltName().toLowerCase().contains(searchQuery)
                    )) {
                    songList.add(id);
                    i++;
                }
            }
        }

        this.sortList(this.filterPlaylistItems(songList));
        this.songListUI.refreshEntries();
    }

    private void sortList(List<ResourceLocation> songList) {
        List<String> sortList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        Map<String, ResourceLocation> titleSongMap = new LinkedHashMap<>();
        Map<String, ResourceLocation> sortObjSongMap = new LinkedHashMap<>();
        Map<String, String> titleSortMap = new LinkedHashMap<>();

        for (ResourceLocation id : songList) {
            String sortObj = "";
            SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(id);
            String songTitle = song.getTitle();
            switch (this.sortQuery.getCategory()) {
                case ARTIST:
                    sortObj = song.getArtist();
                    break;
                case ALBUM:
                    sortObj = song.getAlbum();
            }

            sortList.add(sortObj);
            sortObjSongMap.put(sortObj, id);
            titleList.add(songTitle);
            titleSongMap.put(songTitle, id);
            titleSortMap.put(songTitle, sortObj);
        }

        if (this.sortQuery.getDirection() == SortDirection.ASCENDING) {
            Collections.sort(sortList);
            Collections.sort(titleList);
        } else {
            sortList.sort(Collections.reverseOrder());
            titleList.sort(Collections.reverseOrder());
        }

        Map<ResourceLocation, Integer> tempSongMap = new LinkedHashMap<>();
        int i = 0;
        if (this.sortQuery.getCategory() == SortCategory.TITLE) {
            for (String sortObj : titleList) {
                tempSongMap.put(titleSongMap.get(sortObj), i);
                i++;
            }
        }

        if (this.sortQuery.getCategory() == SortCategory.ALBUM || this.sortQuery.getCategory() == SortCategory.ARTIST) {
            List<String> removeList = new ArrayList<>();

            for (String albumName : sortList) {
                for (String title : titleList) {
                    if (Objects.equals(titleSortMap.get(title), albumName)) {
                        tempSongMap.put(titleSongMap.get(title), i);
                        removeList.add(title);
                        i++;
                    }
                }

                for (String titlex : removeList) {
                    titleList.remove(titlex);
                }
            }
        }

        this.songMap = tempSongMap;
    }

    public ContextMenu createContextMenu(int x, int y) {
        for (GuiEventListener element : this.children()) {
            if (element instanceof ContextMenu) {
                this.removeWidget(element);
                break;
            }
        }

        ContextMenu contextMenu = new ContextMenu(x, y);
        contextMenu.addCloseAction(selfContextMenu -> {
            this.isContextMenuActive = false;
            this.songListUI.permitScroll(true);
            this.playlistSelector.permitScroll(true);
            this.removeWidget(selfContextMenu);
        });
        this.isContextMenuActive = true;
        this.songListUI.permitScroll(false);
        this.playlistSelector.permitScroll(false);
        return (ContextMenu)this.addRenderableWidget(contextMenu);
    }

    public boolean isContextMenuActive() {
        return this.isContextMenuActive;
    }

    public void toggleSelectionMode() {
        this.selectionMode = !this.selectionMode;
        this.closeSelectionTool.visible = !this.closeSelectionTool.visible;
        this.deleteSelected.visible = !this.deleteSelected.visible;
        this.addSelected.visible = !this.addSelected.visible;
        this.toastManager.setYOffset(this.selectionMode ? 40 : 0);
        this.selectedSongs.clear();
    }

    public void updatePlayMode(Mode playMode) {
        switch (playMode) {
            case CATEGORY_BASED:
                this.repeatButton.setIcon(this.textureManager.getTexture("categoryBased"));
                break;
            case REPEAT:
                this.repeatButton.setIcon(this.textureManager.getTexture("repeat"));
                break;
            case REPEAT_ONCE:
                this.repeatButton.setIcon(this.textureManager.getTexture("repeatOnce"));
        }
    }

    public void toggleSelection(ResourceLocation songId) {
        if (this.selectedSongs.contains(songId)) {
            this.selectedSongs.remove(songId);
        } else {
            this.selectedSongs.add(songId);
        }
    }

    private int getStateInt(String id, int defaultTo) {
        Integer state = this.stateManager.getStateInt(id);
        this.stateManager.clearStateInt(id);
        if (this.freshScreen) {
            return defaultTo;
        } else {
            return state == null ? defaultTo : state;
        }
    }

    private String getStateString(String id, String defaultTo) {
        String state = this.stateManager.getStateString(id);
        this.stateManager.clearStateString(id);
        if (this.freshScreen) {
            return defaultTo;
        } else {
            return state == null ? defaultTo : state;
        }
    }

    private void showAlbums() {
        this.albumMode = true;
        this.selectionMode = false;
        this.selectedSongs.clear();
        if (this.albumBrowser != null && this.albumBrowser.isAlbumOpen()) {
            this.albumBrowser.showGrid();
        } else {
            this.rebuildKeepingState();
        }
    }

    /** Re-runs init() while keeping the viewed playlist, search text and scroll position. */
    private void rebuildKeepingState() {
        this.stateManager.setStateInt("songListScrollValue", (int)this.songListUI.getScrollAmount());
        this.stateManager.setStateInt("currentlyViewedPlaylist", this.currentPlaylist);
        this.stateManager.setStateString("searchQuery", this.searchField.getValue());
        this.rebuildWidgets();
    }

    public void setScreen(Screen newScreen) {
        this.stateManager.setStateInt("songListScrollValue", (int)this.songListUI.getScrollAmount());
        this.stateManager.setStateInt("currentlyViewedPlaylist", this.currentPlaylist);
        this.stateManager.setStateString("searchQuery", this.searchField.getValue());
        this.mc.setScreen(newScreen);
    }

    public ArrayList<ResourceLocation> getSelectedSongs() {
        return this.selectedSongs;
    }

    public boolean isSelectionMode() {
        return this.selectionMode;
    }
}
