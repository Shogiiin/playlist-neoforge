package com.fudgeu.playlist.gui.screens.addtoplaylist;

import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.PlaylistScreen;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.screens.editcategories.EditCategoriesScreen;
import com.fudgeu.playlist.gui.widgets.SelectionList;
import com.fudgeu.playlist.gui.widgets.TextLabel;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.PlaylistObj;
import com.fudgeu.playlist.objects.SongObj;
import com.fudgeu.playlist.utils.CategorySetsUtil;
import com.fudgeu.playlist.utils.GeneralUtils;
import com.fudgeu.playlist.utils.RGBA;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class AddToPlaylistScreen extends MenuScreen {
    private final Minecraft mc = Minecraft.getInstance();
    private final ToastManager toastManager;
    private final TextureManager textureManager;
    private final PlaylistManager playlistManager;
    private final ArrayList<ResourceLocation> songsToAdd;
    private final int fromPlaylist;
    private String title;
    private String altName;
    private String artist;
    private String album;
    private ResourceLocation albumArt;
    private final HashMap<ResourceLocation, ArrayList<CategorySet>> categorySets;
    private ArrayList<CategorySet> allSongsCategorySets;
    private final Screen oldScreen;
    private SelectionList playlistList;
    private TransparentButton mainButton;

    public AddToPlaylistScreen(ArrayList<ResourceLocation> songsToAdd, int fromPlaylist, Screen previousScreen) {
        super(Component.translatable("screen.add_to_playlist.title"), previousScreen);
        this.toastManager = PlaylistClient.instance.toastManager;
        this.textureManager = PlaylistClient.instance.textureManager;
        this.playlistManager = PlaylistClient.instance.playlistManager;
        this.songsToAdd = songsToAdd;
        this.fromPlaylist = fromPlaylist;
        this.oldScreen = previousScreen;
        this.categorySets = new HashMap<>();

        for (ResourceLocation song : songsToAdd) {
            this.categorySets.put(song, (ArrayList<CategorySet>)this.playlistManager.getRegisteredPlaylist(fromPlaylist).getCategorySets(song).clone());
        }

        if (songsToAdd.size() == 1) {
            SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(songsToAdd.get(0));
            this.title = song.getTitle();
            this.altName = song.getAltName();
            this.artist = song.getArtist();
            this.album = song.getAlbum();
            this.albumArt = GeneralUtils.matchUpAlbumToArt(this.album);
        }
    }

    @Override
    public void init() {
        this.setMainContentHeight(160);
        super.init();
        if (this.songsToAdd.size() == 1) {
            this.addTextLabel(32, 3, Component.literal(this.title), 16777215);
            if (this.altName != null) {
                this.addTextLabel(
                    32 + this.mc.font.width(this.title) + 5,
                    3,
                    Component.literal("(" + this.altName + ")").withStyle(ChatFormatting.ITALIC),
                    11579568
                );
            }

            this.addTextLabel(32, 9 + 6, Component.literal(this.artist + " - " + this.album), 11579568);
            this.addTexture(0, 0, 25, 25, this.albumArt);
        } else {
            int albumArtAmount = Math.min(4, this.songsToAdd.size()) - 1;
            int lastTextureX = 0;
            int lastTextureWidth = 0;
            boolean lastTextureSet = false;
            float colorRange = 0.95F;

            for (int i = albumArtAmount; i >= 0; i--) {
                ResourceLocation albumTexture = GeneralUtils.matchUpAlbumToArt(this.playlistManager.getRegisteredSongObj(this.songsToAdd.get(i)).getAlbum());
                int textureX = (int)Math.sqrt(360 * i);
                int textureWidth = 25 - i * 2;
                if (!lastTextureSet) {
                    lastTextureX = textureX;
                    lastTextureWidth = textureWidth;
                    lastTextureSet = true;
                }

                float colorFilter = 0.05F + colorRange * (1.0F - i / 4.0F);
                this.addTexture(textureX, i * 2, textureWidth, 25 - i * 4, albumTexture, new RGBA(colorFilter));
            }

            int textX = lastTextureX + lastTextureWidth + 7;
            this.addTextLabel(textX, 3, Component.translatable("screen.add_to_playlist.multiple_songs", new Object[]{this.songsToAdd.size()}), 16777215);
            int limit = Math.min(3, this.songsToAdd.size());
            StringBuilder subText = new StringBuilder();
            boolean stringOverflow = false;

            for (int j = 0; j < limit; j++) {
                if (j == limit - 1 && this.songsToAdd.size() <= 3) {
                    subText.append("& ");
                }

                subText.append(this.playlistManager.getRegisteredSongObj(this.songsToAdd.get(j)).getTitle());
                if (this.mc.font.width(subText.toString()) >= this.menuWidth - textX) {
                    while (this.mc.font.width(subText.toString()) >= this.menuWidth - textX - 8) {
                        subText.deleteCharAt(subText.length() - 1);
                    }

                    subText.append("...");
                    stringOverflow = true;
                    break;
                }

                if (j != limit - 1 && limit != 2) {
                    subText.append(", ");
                }

                if (j == 0 && limit == 2) {
                    subText.append(" ");
                }
            }

            if (!stringOverflow && this.songsToAdd.size() > 3) {
                subText.append(", ...");
            }

            this.addTextLabel(textX, 9 + 6, Component.literal(subText.toString()).withStyle(ChatFormatting.ITALIC), 11579568);
        }

        this.addDivider(35);
        this.addTextLabel(0, 45, Component.translatable("screen.add_to_playlist.select_text"), 11184810);
        HashMap<String, Component> playlists = new HashMap<>();

        for (int id : this.playlistManager.getRegisteredPlaylists()) {
            playlists.put(String.valueOf(id), Component.literal(this.playlistManager.getRegisteredPlaylist(id).getTitle()));
        }

        this.playlistList = this.addSelectionList(0, 60, this.menuWidth, this.menuHeight - 34, 16, playlists);
        TextLabel usingCategories = this.addSingleLineTextLabel(0, this.menuHeight - 14, Component.translatable(this.determineTranslationKey()), 16777215);
        TransparentButton editCategoriesButton = this.addButton(this.menuWidth - 20, this.menuHeight - 20, 20, 20, null, onPress -> {
            ArrayList<CategorySet> toEdit;
            if (this.songsToAdd.size() == 1) {
                toEdit = this.categorySets.get(this.songsToAdd.get(0));
            } else {
                if (this.allSongsCategorySets == null) {
                    this.allSongsCategorySets = new ArrayList<>();
                }

                toEdit = this.allSongsCategorySets;
            }

            this.mc.setScreen(new EditCategoriesScreen(this, this.songsToAdd.get(0), this.songsToAdd.size() == 1, toEdit));
        });
        editCategoriesButton.setIcon(this.textureManager.getTexture("edit"));
        editCategoriesButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        TransparentButton resetButton = this.addButton(
            this.menuWidth - 45,
            this.menuHeight - 20,
            20,
            20,
            null,
            onPress -> {
                this.allSongsCategorySets = null;
                this.categorySets.clear();

                for (ResourceLocation song : this.songsToAdd) {
                    this.categorySets
                        .put(song, (ArrayList<CategorySet>)this.playlistManager.getRegisteredPlaylist(this.fromPlaylist).getCategorySets(song).clone());
                }

                usingCategories.setLabel(Component.translatable(this.determineTranslationKey()));
            }
        );
        resetButton.setIcon(this.textureManager.getTexture("reset"));
        resetButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        this.mainButton = this.setPrimaryMenuButton(Component.translatable("generic.add"), onPress -> {
            SelectionList.Selection selected = (SelectionList.Selection)this.playlistList.getFocused();
            int selectedPlaylist = Integer.parseInt(selected.getId());
            int errorCount = 0;

            for (ResourceLocation song : this.songsToAdd) {
                boolean added = this.playlistManager.addSongToPlaylist(selectedPlaylist, song);
                if (added) {
                    PlaylistObj playlist = this.playlistManager.getRegisteredPlaylist(selectedPlaylist);
                    if (this.allSongsCategorySets == null) {
                        playlist.setCategorySets(song, this.categorySets.get(song));
                    } else {
                        playlist.setCategorySets(song, (ArrayList<CategorySet>)this.allSongsCategorySets.clone());
                    }
                }

                if (!added) {
                    errorCount++;
                }
            }

            if (this.oldScreen instanceof PlaylistScreen playlistScreen) {
                String playlistTitle = this.playlistManager.getRegisteredPlaylist(selectedPlaylist).getTitle();
                if (errorCount == 0) {
                    if (this.songsToAdd.size() == 1) {
                        this.toastManager.addToast(Component.translatable("toast.song_added", new Object[]{this.title, playlistTitle}), 60);
                    } else {
                        this.toastManager.addToast(Component.translatable("toast.songs_added", new Object[]{this.songsToAdd.size(), playlistTitle}), 60);
                    }
                } else if (this.songsToAdd.size() == 1) {
                    this.toastManager.addToast(Component.translatable("toast.song_not_added", new Object[]{playlistTitle, this.title}), 60);
                } else {
                    this.toastManager.addToast(Component.translatable("toast.songs_not_added", new Object[]{errorCount, playlistTitle}), 60);
                }

                if (playlistScreen.isSelectionMode()) {
                    playlistScreen.toggleSelectionMode();
                }
            }

            this.closeScreen();
        });
        this.mainButton.disabled = true;
    }

    @Override
    public void tick() {
        this.mainButton.disabled = this.playlistList.getFocused() == null;
    }

    private boolean areCategoriesDefault() {
        for (ResourceLocation songId : this.categorySets.keySet()) {
            ArrayList<CategorySet> sets;
            if (this.allSongsCategorySets != null) {
                sets = this.allSongsCategorySets;
            } else {
                sets = this.categorySets.get(songId);
            }

            ArrayList<CategorySet> defaultSets = this.playlistManager.getRegisteredSongObj(songId).getCategories();
            if (!CategorySetsUtil.equals(sets, defaultSets)) {
                return false;
            }
        }

        return true;
    }

    private String determineTranslationKey() {
        String translationKey = "screen.add_to_playlist.using_default";
        if (!this.areCategoriesDefault()) {
            translationKey = "screen.add_to_playlist.using_custom";
        }

        return translationKey;
    }
}
