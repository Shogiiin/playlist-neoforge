package com.fudgeu.playlist.gui.screens.editcategories;

import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.screens.addcategory.AddCategoryScreen;
import com.fudgeu.playlist.gui.screens.songinfo.ConditionList;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.utils.CategorySetsUtil;
import java.util.ArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class EditCategoriesScreen extends MenuScreen {
    private final Minecraft mc;
    private final TextureManager textureManager;
    private final PlaylistManager playlistManager;
    private final ResourceLocation songId;
    private final boolean resettable;
    private ArrayList<CategorySet> categorySets;
    private boolean isEditingCategories = false;

    public EditCategoriesScreen(Screen previousScreen, ResourceLocation songId, boolean resettable, ArrayList<CategorySet> categorySets) {
        super(Component.translatable("screen.edit_categories.title"), previousScreen);
        this.mc = Minecraft.getInstance();
        this.textureManager = PlaylistClient.instance.textureManager;
        this.playlistManager = PlaylistClient.instance.playlistManager;
        this.songId = songId;
        this.resettable = resettable;
        this.categorySets = categorySets;
    }

    @Override
    public void init() {
        this.setMainContentHeight(80);
        super.init();
        this.addTextLabel(0, 0, Component.translatable("screen.song_info.categories"), 11184810);
        ConditionList conditionList = this.addConditionList(0, 15, this.menuWidth, 80, 12, true, this.categorySets);
        TransparentButton addCategoryButton = this.addButton(
            this.menuWidth - 8, 0, 8, 8, null, selfButton -> this.mc.setScreen(new AddCategoryScreen(this, false, null, this.categorySets))
        );
        addCategoryButton.setIcon(this.textureManager.getTexture("add"));
        addCategoryButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        addCategoryButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        if (this.resettable) {
            TransparentButton resetButton = this.addButton(this.menuWidth - 20, 0, 8, 8, null, selfButton -> {
                CategorySetsUtil.conform(this.categorySets, this.playlistManager.getRegisteredSongObj(this.songId).getCategories());
                conditionList.refreshEntries(this.categorySets);
            });
            resetButton.setIcon(this.textureManager.getTexture("reset"));
            resetButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
            resetButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        }

        this.setPrimaryMenuButton(Component.translatable("generic.close"), onPress -> this.closeScreen());
    }
}
