package com.fudgeu.playlist.gui.screens.songinfo;

import com.fudgeu.playlist.ConditionManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.screens.addcategory.AddCategoryScreen;
import com.fudgeu.playlist.gui.widgets.ListWidget;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.utils.CategorySetsUtil;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class ConditionList extends ListWidget {
    protected final boolean editable;

    public ConditionList(int x, int y, int width, int endY, int itemHeight, boolean editable, ArrayList<CategorySet> categorySets) {
        super(Minecraft.getInstance(), width - 10, endY - y, y + 3, endY + 3, itemHeight);
        this.editable = editable;
        super.setLeftPos(x + 5);
        super.setHoveredColor(new RGBA(0.1F));
        this.refreshEntries(categorySets);
    }

    @Override
    public void preRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawRect(this.left - 5, this.top - 3, this.right + 5, this.bottom + 3, 0.1F, 0.1F, 0.1F, 1.0F);
    }

    public void refreshEntries(ArrayList<CategorySet> categorySets) {
        this.clearEntries();

        for (CategorySet categorySet : categorySets) {
            this.addEntry(new ConditionList.ListCondition(false, categorySet.getCategoryId(), null, this, categorySets));

            for (String subcategory : categorySet.getSubcategories()) {
                this.addEntry(new ConditionList.ListCondition(true, categorySet.getCategoryId(), subcategory, this, categorySets));
            }
        }
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    static class ListCondition extends ListWidget.Entry<ConditionList.ListCondition> {
        private Component text;
        private boolean isSubcategory;
        private Minecraft mc;
        private TransparentButton addConditionButton;
        private TransparentButton deleteButton;
        private ConditionList parent;

        public ListCondition(boolean isSubcategory, String category, String subcategory, ConditionList parent, ArrayList<CategorySet> categorySets) {
            TextureManager textureManager = PlaylistClient.instance.textureManager;
            this.isSubcategory = isSubcategory;
            this.parent = parent;
            if (isSubcategory) {
                this.text = Component.literal("- " + ConditionManager.getDisplayName(subcategory).getString());
            } else {
                this.text = ConditionManager.getDisplayName(category);
            }

            this.deleteButton = new TransparentButton(0, 0, 8, 8, null, selfButton -> {
                if (isSubcategory) {
                    CategorySetsUtil.removeSubcategory(categorySets, category, subcategory);
                    parent.refreshEntries(categorySets);
                } else {
                    CategorySetsUtil.removeCategory(categorySets, category);
                    parent.refreshEntries(categorySets);
                }
            });
            this.deleteButton.setIcon(textureManager.getTexture("trash"));
            this.deleteButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
            this.deleteButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
            this.deleteButton.visible = false;
            if (!isSubcategory) {
                this.addConditionButton = new TransparentButton(
                    0, 0, 8, 8, null, selfButton -> this.mc.setScreen(new AddCategoryScreen(this.mc.screen, true, category, categorySets))
                );
                this.addConditionButton.setIcon(textureManager.getTexture("add"));
                this.addConditionButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
                this.addConditionButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
                this.addConditionButton.visible = false;
            }

            this.mc = Minecraft.getInstance();
        }

        @Override
        public void render(GuiGraphics context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            boolean isHovered = mouseX >= x
                && mouseY >= y
                && mouseX < x + entryWidth
                && mouseY < y + entryHeight
                && !PlaylistClient.instance.playlistScreenInstance.isContextMenuActive();
            if (isHovered && this.parent.editable) {
                this.deleteButton.visible = true;
                if (!this.isSubcategory) {
                    this.addConditionButton.visible = true;
                }
            } else {
                this.deleteButton.visible = false;
                if (!this.isSubcategory) {
                    this.addConditionButton.visible = false;
                }
            }

            this.deleteButton.render(context, x + entryWidth - 12, y + 2, mouseX, mouseY, delta);
            if (this.isSubcategory) {
                context.drawString(this.mc.font, this.text, x + 10, y + 2, 11184810, false);
            } else {
                context.drawString(this.mc.font, this.text, x, y + 2, 16777215, false);
                this.addConditionButton.render(context, x + entryWidth - 22, y + 2, mouseX, mouseY, delta);
            }
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0
                && mouseX >= this.deleteButton.getX()
                && mouseX <= this.deleteButton.getX() + this.deleteButton.getWidth()
                && mouseY >= this.deleteButton.getY()
                && mouseY <= this.deleteButton.getY() + this.deleteButton.getHeight()) {
                this.deleteButton.onClick(mouseX, mouseY);
                return true;
            } else if (!this.isSubcategory
                && button == 0
                && mouseX >= this.addConditionButton.getX()
                && mouseX <= this.addConditionButton.getX() + this.addConditionButton.getWidth()
                && mouseY >= this.addConditionButton.getY()
                && mouseY <= this.addConditionButton.getY() + this.addConditionButton.getHeight()) {
                this.addConditionButton.onClick(mouseX, mouseY);
                return true;
            } else {
                return false;
            }
        }

        public void setFocused(boolean focused) {
        }

        public boolean isFocused() {
            return false;
        }

        public Component getText() {
            return this.text;
        }
    }
}
