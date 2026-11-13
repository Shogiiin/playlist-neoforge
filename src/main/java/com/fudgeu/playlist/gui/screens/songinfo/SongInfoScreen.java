package com.fudgeu.playlist.gui.screens.songinfo;

import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.screens.addcategory.AddCategoryScreen;
import com.fudgeu.playlist.gui.widgets.TextLabel;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.objects.PlaylistObj;
import com.fudgeu.playlist.objects.SongObj;
import com.fudgeu.playlist.utils.CategorySetsUtil;
import com.fudgeu.playlist.utils.GeneralUtils;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class SongInfoScreen extends MenuScreen {
    private final int playlistId;
    private final ResourceLocation songId;
    private final Minecraft mc;
    private TextLabel weightNumber;

    public SongInfoScreen(Screen previousScreen, int playlistId, ResourceLocation songId) {
        super(Component.translatable("screen.song_info.title"), previousScreen);
        this.playlistId = playlistId;
        this.songId = songId;
        this.mc = Minecraft.getInstance();
    }

    @Override
    public void init() {
        SongObj song = PlaylistClient.instance.playlistManager.getRegisteredSongObj(this.songId);
        PlaylistObj playlist = PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.playlistId);
        ArrayList<CategorySet> categorySets = PlaylistClient.instance
            .playlistManager
            .getRegisteredPlaylist(this.playlistId)
            .getCustomCategories()
            .get(this.songId);
        TextureManager textureManager = PlaylistClient.instance.textureManager;
        PlaylistManager playlistManager = PlaylistClient.instance.playlistManager;
        int height = 170;
        this.setMainContentHeight(height - 10);
        super.init();
        this.addTexture(0, 0, 75, 75, GeneralUtils.matchUpAlbumToArt(song.getAlbum()));
        int count = 0;
        if (song.getAltName() != null) {
            count++;
        }

        if (song.getType() != null) {
            count++;
        }

        int startY = (75 - (48 + 12 * count)) / 2;
        this.addTextLabel(85, startY, Component.literal(song.getTitle()), 16777215);
        this.addTextLabel(85, startY + 12, Component.literal(song.getArtist()), 11184810);
        this.addTextLabel(85, startY + 24, Component.literal(song.getAlbum()).withStyle(ChatFormatting.ITALIC), 11184810);
        this.addTextLabel(85, startY + 42, Component.translatable("screen.song_info.weight"), 11184810);
        int weightTextLength = this.mc.font.width(Component.translatable("screen.song_info.weight").getString());
        if (this.playlistId != -1) {
            TransparentButton weightUpButton = this.addButton(
                93 + weightTextLength,
                startY + 37,
                8,
                8,
                null,
                onPress -> playlist.setCustomWeight(this.songId, Math.min(playlist.getCustomWeights().get(this.songId) + 1, 9))
            );
            weightUpButton.setIcon(textureManager.getTexture("arrow_up_short"));
            weightUpButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
            weightUpButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
            TransparentButton weightDownButton = this.addButton(
                93 + weightTextLength,
                startY + 46,
                8,
                8,
                null,
                onPress -> playlist.setCustomWeight(this.songId, Math.max(playlist.getCustomWeights().get(this.songId) - 1, 1))
            );
            weightDownButton.setIcon(textureManager.getTexture("arrow_down_short"));
            weightDownButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
            weightDownButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        }

        int weightToUse = this.playlistId != -1 ? playlist.getCustomWeights().get(this.songId) : song.getWeight();
        this.weightNumber = this.addSingleLineTextLabel(85 + weightTextLength, startY + 42, Component.literal(String.valueOf(weightToUse)), 11184810);
        int y = 1;
        if (song.getType() != null) {
            this.addTextLabel(85, startY + 42 + y * 12, Component.translatable("screen.song_info.type", new Object[]{song.getType()}), 11184810);
            y++;
        }

        if (song.getAltName() != null) {
            this.addTextLabel(85, startY + 42 + y * 12, Component.translatable("screen.song_info.alt", new Object[]{song.getAltName()}), 11184810);
        }

        this.addTextLabel(0, 90, Component.translatable("screen.song_info.categories"), 11184810);
        ConditionList categoryList = this.addConditionList(0, 105, this.menuWidth, 165, 12, this.playlistId != -1, categorySets);
        if (this.playlistId != -1) {
            TransparentButton addCategoryButton = this.addButton(
                this.menuWidth - 8, 90, 8, 8, null, selfButton -> this.mc.setScreen(new AddCategoryScreen(this.mc.screen, false, null, categorySets))
            );
            addCategoryButton.setIcon(textureManager.getTexture("add"));
            addCategoryButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
            addCategoryButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
            TransparentButton resetCategoriesButton = this.addButton(this.menuWidth - 20, 90, 8, 8, null, selfButton -> {
                CategorySetsUtil.conform(categorySets, playlistManager.getRegisteredSongObj(this.songId).getCategories());
                categoryList.refreshEntries(categorySets);
            });
            resetCategoriesButton.setIcon(textureManager.getTexture("reset"));
            resetCategoriesButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
            resetCategoriesButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        }

        this.setPrimaryMenuButton(Component.translatable("generic.close"), onPress -> this.closeScreen());
    }

    @Override
    public void tick() {
        PlaylistObj playlist = PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.playlistId);
        super.tick();
        if (this.playlistId != -1) {
            this.weightNumber.setLabel(Component.literal(String.valueOf(playlist.getCustomWeights().get(this.songId))));
        }
    }
}
