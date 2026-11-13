package com.fudgeu.playlist.gui;

import com.fudgeu.playlist.ConditionManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.screens.addtoplaylist.AddToPlaylistScreen;
import com.fudgeu.playlist.gui.screens.songinfo.SongInfoScreen;
import com.fudgeu.playlist.gui.widgets.ContextMenu;
import com.fudgeu.playlist.gui.widgets.ListWidget;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.SongObj;
import com.fudgeu.playlist.utils.CategorySetsUtil;
import com.fudgeu.playlist.utils.GeneralUtils;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

public class SongEntryUI extends ListWidget.Entry<SongEntryUI> {
    private final Minecraft mc;
    private final PlaylistScreen playlistScreen;
    private final TextureManager textureManager;
    private final PlaylistManager playlistManager;
    private final ToastManager toastManager;
    private final StateManager stateManager;
    private final int playlistId;
    private final ResourceLocation songId;
    private final String title;
    private final String artist;
    private final String album;
    private final String altName;
    private boolean isHovered = false;
    private final ResourceLocation albumArt;
    private TransparentButton addSongButton;
    private TransparentButton infoButton;
    private List<TransparentButton> buttons = new ArrayList<>();
    private int currentYPos = 0;

    public SongEntryUI(int playlistId, ResourceLocation songId) {
        this.mc = Minecraft.getInstance();
        this.playlistScreen = PlaylistClient.instance.playlistScreenInstance;
        this.textureManager = PlaylistClient.instance.textureManager;
        this.playlistManager = PlaylistClient.instance.playlistManager;
        this.stateManager = PlaylistClient.instance.stateManager;
        this.toastManager = PlaylistClient.instance.toastManager;
        this.playlistId = playlistId;
        this.songId = songId;
        SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(songId);
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.album = song.getAlbum();
        this.altName = song.getAltName();
        this.albumArt = GeneralUtils.matchUpAlbumToArt(this.album);
        this.addSongButton = new TransparentButton(0, 0, 20, 20, Component.literal("+"), onPress -> {
            ArrayList<ResourceLocation> songsToAdd = new ArrayList<>();
            songsToAdd.add(songId);
            PlaylistClient.instance.playlistScreenInstance.setScreen(new AddToPlaylistScreen(songsToAdd, playlistId, this.mc.screen));
        });
        this.addSongButton.setDefaultColor(new RGBA(0.1F));
        this.addSongButton.setHoveredColor(new RGBA(0.125F));
        this.infoButton = new TransparentButton(
            0,
            0,
            20,
            20,
            Component.literal("i"),
            onPress -> PlaylistClient.instance
                .playlistScreenInstance
                .setScreen(new SongInfoScreen(PlaylistClient.instance.playlistScreenInstance, this.playlistId, this.songId))
        );
        this.infoButton.setDefaultColor(new RGBA(0.1F));
        this.infoButton.setHoveredColor(new RGBA(0.125F));
        this.addButton(this.addSongButton);
        this.addButton(this.infoButton);
    }

    @Override
    public void render(GuiGraphics context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        this.isHovered = mouseX >= x
            && mouseY >= y
            && mouseX < x + entryWidth
            && mouseY < y + entryHeight
            && !PlaylistClient.instance.playlistScreenInstance.isContextMenuActive();
        this.currentYPos = y;
        int fontHeight = 9;
        if (this.playlistScreen.getSelectedSongs().contains(this.songId)) {
            RenderUtils.drawRect(x, y, x + entryWidth, y + entryHeight, 0.0F, 0.0F, 0.0F, 0.6F);
            RenderUtils.drawRect(x + 8, y + (entryHeight - 4) / 2, x + 12, y + (entryHeight + 4) / 2, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        context.drawString(this.mc.font, Component.literal(this.title), x + 61, y + 10, 16777215, false);
        if (this.altName != null && ConfigManager.INSTANCE.showAltNames.get()) {
            context.drawString(
                this.mc.font,
                Component.literal("(" + this.altName + ")").withStyle(ChatFormatting.ITALIC),
                x + 61 + this.mc.font.width(this.title) + 5,
                y + 10,
                11579568,
                false
            );
        }

        context.drawString(this.mc.font, this.artist, x + 61, y + 9 + 13, 11579568, false);
        int albumTextYPos = entryHeight / 2 - fontHeight / 2;
        context.drawString(this.mc.font, this.album, x + 231, y + albumTextYPos, 11579568, false);
        ArrayList<ResourceLocation> textures = new ArrayList<>();
        ArrayList<CategorySet> categorySets;
        if (this.playlistId == -1) {
            categorySets = this.playlistManager.getRegisteredSongObj(this.songId).getCategories();
        } else {
            categorySets = this.playlistManager.getRegisteredPlaylist(this.playlistId).getCustomCategories().get(this.songId);
        }

        for (CategorySet set : categorySets) {
            if (set.getCategoryId().equals(this.stateManager.getStateString("currentCategoryTab"))
                || this.stateManager.getStateString("currentCategoryTab").equals("all")) {
                for (String condition : set.getSubcategories()) {
                    if (PlaylistClient.instance.conditionManager.exists(condition)
                        && !textures.contains(this.textureManager.getTexture(ConditionManager.parse(condition)))
                        && this.textureManager.exists(ConditionManager.parse(condition))) {
                        textures.add(this.textureManager.getTexture(ConditionManager.parse(condition)));
                    }
                }
            }
        }

        context.blit(this.albumArt, x + 21, y + 8, 0.0F, 0.0F, 25, 25, 25, 25);
        int i = 0;

        for (ResourceLocation texture : textures) {
            context.blit(texture, PlaylistScreen.screenWidth - 30 - this.buttons.size() * 25 - i * 16, y + 16, 0.0F, 0.0F, 8, 8, 8, 8);
            i++;
        }

        RenderSystem.setShader(GameRenderer::getPositionShader);
        i = 0;
        if (this.isHovered) {
            for (TransparentButton button : this.buttons) {
                button.render(context, PlaylistScreen.screenWidth - 30 - i * 25, y + 10, mouseX, mouseY, delta);
                i++;
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = 0;

        for (TransparentButton tButton : this.buttons) {
            if (mouseX >= PlaylistScreen.screenWidth - 30 - i * 25
                && mouseY >= this.currentYPos + 10
                && mouseX <= PlaylistScreen.screenWidth - 10 - i * 25
                && mouseY < this.currentYPos + 30) {
                tButton.onClick(mouseX, mouseY);
                return true;
            }

            i++;
        }

        if (button == 0) {
            if (this.playlistScreen.isSelectionMode()) {
                this.playlistScreen.toggleSelection(this.songId);
            } else {
                PlaylistClient.instance.musicManager.playSongNow(this.songId);
            }

            return true;
        } else if (button != 1) {
            return false;
        } else {
            ContextMenu contextMenu = PlaylistClient.instance.playlistScreenInstance.createContextMenu((int)mouseX, (int)mouseY);
            contextMenu.addOption(
                this.textureManager.getTexture("play"),
                Component.translatable("generic.play"),
                true,
                selfContextMenu -> PlaylistClient.instance.musicManager.playSongNow(this.songId)
            );
            contextMenu.addOption(
                this.textureManager.getTexture("queue"),
                Component.translatable("screen.playlist.add_queue"),
                true,
                onPress -> PlaylistClient.instance.musicManager.addSongToQueue(this.songId)
            );
            contextMenu.addOption(this.textureManager.getTexture("add"), Component.translatable("screen.playlist.add_playlist"), true, onPress -> {
                ArrayList<ResourceLocation> songsToAdd = new ArrayList<>();
                songsToAdd.add(this.songId);
                PlaylistClient.instance.playlistScreenInstance.setScreen(new AddToPlaylistScreen(songsToAdd, this.playlistId, this.mc.screen));
            });
            contextMenu.addOption(
                this.textureManager.getTexture("info"),
                Component.translatable("generic.info"),
                true,
                onPress -> PlaylistClient.instance
                    .playlistScreenInstance
                    .setScreen(new SongInfoScreen(PlaylistClient.instance.playlistScreenInstance, this.playlistId, this.songId))
            );
            if (PlaylistClient.instance.playlistScreenInstance.getSelectedPlaylist() != -1) {
                PlaylistScreen playlistScreenInstance = PlaylistClient.instance.playlistScreenInstance;
                boolean shouldDeleteBeEnabled = this.playlistId != this.playlistManager.getDefaultPlaylist()
                    || this.playlistManager.getRegisteredPlaylist(this.playlistId).getSongCount() != 1;
                contextMenu.addOption(this.textureManager.getTexture("trash"), Component.translatable("generic.remove"), shouldDeleteBeEnabled, onPress -> {
                    this.playlistManager.removeSongFromPlaylist(playlistScreenInstance.getSelectedPlaylist(), this.songId);
                    playlistScreenInstance.refreshSongList();
                    this.toastManager.addToast(Component.translatable("toast.song_removed"), 60);
                });
                boolean shouldCategoryDeleteBeEnabled = shouldDeleteBeEnabled && !this.stateManager.getStateString("currentCategoryTab").equals("all");
                contextMenu.addOption(
                    this.textureManager.getTexture("trashCondition"),
                    Component.translatable("generic.remove_from_category"),
                    shouldCategoryDeleteBeEnabled,
                    onPress -> {
                        ArrayList<CategorySet> sets = this.playlistManager
                            .getRegisteredPlaylist(playlistScreenInstance.getSelectedPlaylist())
                            .getCategorySets(this.songId);
                        CategorySetsUtil.removeCategory(sets, this.stateManager.getStateString("currentCategoryTab"));
                        if (sets.size() == 0) {
                            this.playlistManager.removeSongFromPlaylist(playlistScreenInstance.getSelectedPlaylist(), this.songId);
                        }

                        playlistScreenInstance.refreshSongList();
                        this.toastManager.addToast(Component.translatable("toast.category_removed_from_song"), 60);
                    }
                );
            }

            contextMenu.addCloseAction(onClose -> this.isSelfContextMenuOpen = false);
            contextMenu.build();
            this.isSelfContextMenuOpen = true;
            return true;
        }
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }

    private void addButton(TransparentButton button) {
        this.buttons.add(button);
    }

    public ResourceLocation getSongId() {
        return this.songId;
    }
}
