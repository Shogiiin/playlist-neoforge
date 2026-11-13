package com.fudgeu.playlist.gui.screens.addcategory;

import com.fudgeu.playlist.Condition;
import com.fudgeu.playlist.ConditionManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.widgets.SelectionList;
import com.fudgeu.playlist.gui.widgets.TextLabel;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.utils.GeneralUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;

public class AddCategoryScreen extends MenuScreen {
    private final Minecraft mc = Minecraft.getInstance();
    private final ToastManager toastManager;
    private final boolean isSubcategory;
    private final String underCategory;
    private final ArrayList<CategorySet> categorySets;
    private SelectionList conditionList;
    private SelectionList metaList;
    private TransparentButton addButton;
    private TextLabel metaNotAvailable;
    private SelectionList.Selection lastSelection;

    public AddCategoryScreen(Screen previousScreen, boolean isSubcategory, String underCategory, ArrayList<CategorySet> categorySets) {
        super(Component.translatable("screen.add_category.title"), previousScreen);
        this.toastManager = PlaylistClient.instance.toastManager;
        this.isSubcategory = isSubcategory;
        this.underCategory = underCategory;
        this.categorySets = categorySets;
    }

    @Override
    public void init() {
        this.setMainContentHeight(160);
        super.init();
        String labelIdentifier = "screen.add_category.select_" + (this.isSubcategory ? "subcategory" : "category");
        this.addTextLabel(0, 0, Component.translatable(labelIdentifier), 16777215);
        HashMap<String, Component> selections = new HashMap<>();

        for (Condition condition : PlaylistClient.instance.conditionManager.getRegisteredConditions().values()) {
            if (!condition.isCategory() && this.isSubcategory) {
                selections.put(condition.getId(), ConditionManager.getDisplayName(condition.getId()));
            } else if (condition.isCategory() && !this.isSubcategory) {
                selections.put(condition.getId(), ConditionManager.getDisplayName(condition.getId()));
            }
        }

        if (this.isSubcategory) {
            selections.put("condition.biome", Component.translatable("generic.biome"));
        }

        this.conditionList = this.addSelectionList(0, 16, this.menuWidth, 76, 12, selections);
        this.addTextLabel(0, 88, Component.translatable("screen.add_category.select_option"), 16777215);
        this.metaList = this.addSelectionList(0, 104, this.menuWidth, 164, 12, new HashMap<>());
        Component notAvailableText = Component.translatable("screen.add_category.not_available").withStyle(ChatFormatting.ITALIC);
        int middleX = (this.menuWidth - this.mc.font.width(notAvailableText)) / 2;
        this.metaNotAvailable = this.addSingleLineTextLabel(middleX, 130, notAvailableText, 11184810);
        this.addButton = this.setPrimaryMenuButton(Component.translatable("generic.add"), onPress -> {
            if (this.isSubcategory) {
                boolean success = false;

                for (CategorySet set : this.categorySets) {
                    if (Objects.equals(set.getCategoryId(), this.underCategory)) {
                        success = set.addSubcategory(this.getSelectedId());
                    }
                }

                this.toastManager.addToast(Component.translatable(success ? "toast.subcategory_added" : "toast.subcategory_not_added"), 60);
            } else {
                boolean success = true;
                String toAdd = this.getSelectedId();

                for (CategorySet setx : this.categorySets) {
                    if (setx.getCategoryId().equals(toAdd)) {
                        success = false;
                        break;
                    }
                }

                if (success) {
                    this.categorySets.add(new CategorySet(toAdd, new ArrayList<>()));
                }

                this.toastManager.addToast(Component.translatable(success ? "toast.category_added" : "toast.category_not_added"), 60);
            }

            this.closeScreen();
        });
    }

    @Override
    public void tick() {
        super.tick();
        SelectionList.Selection selection = (SelectionList.Selection)this.conditionList.getFocused();
        if (selection != this.lastSelection) {
            if (selection != null && Objects.equals(selection.getId(), "condition.biome")) {
                HashMap<String, Component> selections = new HashMap<>();
                Registry<Biome> biomeList = this.mc.level.registryAccess().registryOrThrow(Registries.BIOME);

                for (Biome biome : biomeList) {
                    selections.put(biomeList.getKey(biome).toString(), Component.literal(GeneralUtils.getBiomeName(biomeList.getKey(biome))));
                }

                this.metaList.setSelections(selections);
            } else {
                this.metaList.setSelections(new HashMap<>());
            }

            this.metaNotAvailable.visible = this.metaList.getSelections().size() == 0;
        }

        this.lastSelection = selection;
        SelectionList.Selection metaSelection = (SelectionList.Selection)this.metaList.getFocused();
        this.addButton.disabled = selection == null || this.metaList.getSelections().size() > 0 && metaSelection == null;
    }

    public String getSelectedId() {
        SelectionList.Selection condition = (SelectionList.Selection)this.conditionList.getFocused();
        SelectionList.Selection metaSelection = (SelectionList.Selection)this.metaList.getFocused();
        return metaSelection != null ? condition.getId() + ":" + metaSelection.getId() : condition.getId();
    }
}
