package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.HashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class SelectionList extends ListWidget {
    private HashMap<String, Component> selections;
    private SelectionList.SelectionAction selectionAction;

    public SelectionList(int x, int y, int width, int endY, int itemHeight, HashMap<String, Component> selections) {
        super(Minecraft.getInstance(), width - 10, endY - y, y + 3, endY + 3, itemHeight);
        super.setLeftPos(x + 5);
        this.selections = selections;
        this.setHoveredColor(new RGBA(0.125F));
        this.setSelectedColor(new RGBA(0.15F));

        for (String id : this.selections.keySet()) {
            this.addEntry(new SelectionList.Selection(id, this.selections.get(id), this));
        }
    }

    public void setSelections(HashMap<String, Component> selections) {
        this.selections = selections;
        this.setFocused(null);
        this.clearEntries();

        for (String id : this.selections.keySet()) {
            this.addEntry(new SelectionList.Selection(id, this.selections.get(id), this));
        }
    }

    public void setSelected(String id) {
        for (int i = 0; i < this.getEntryCount(); i++) {
            SelectionList.Selection selection = (SelectionList.Selection)this.getEntry(i);
            if (selection.getId().equals(id)) {
                this.setFocused(selection);
            }
        }
    }

    @Override
    public void preRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawRect(this.left - 5, this.top - 3, this.right + 5, this.bottom + 3, 0.1F, 0.1F, 0.1F, 1.0F);
    }

    public void onSelection(String id) {
        if (this.selectionAction != null) {
            this.selectionAction.onSelect(id);
        }
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    public HashMap<String, Component> getSelections() {
        return this.selections;
    }

    public void setSelectionAction(SelectionList.SelectionAction selectionAction) {
        this.selectionAction = selectionAction;
    }

    public static class Selection extends ListWidget.Entry<SelectionList.Selection> {
        private final String id;
        private final Component text;
        private final SelectionList parent;
        private final Minecraft mc;

        public Selection(String id, Component text, SelectionList parent) {
            this.id = id;
            this.text = text;
            this.parent = parent;
            this.mc = Minecraft.getInstance();
        }

        @Override
        public void render(GuiGraphics context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            context.drawString(this.mc.font, this.text, x + 4, y + Math.round(entryHeight / 2.0F) - 4, 16777215, false);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.parent.setFocused(this);
            this.parent.onSelection(this.id);
            return true;
        }

        public void setFocused(boolean focused) {
        }

        public boolean isFocused() {
            return false;
        }

        public String getId() {
            return this.id;
        }
    }

    public interface SelectionAction {
        void onSelect(String var1);
    }
}
