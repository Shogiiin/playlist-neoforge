package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class RadioGroup implements GuiEventListener, Renderable, NarratableEntry {
    private int x;
    private int y;
    private Map<String, RadioGroup.RadioButton> buttons = new HashMap<>();
    private Minecraft mc;
    private RadioGroup.PressAction pressAction;

    public RadioGroup(int x, int y) {
        this.x = x;
        this.y = y;
        this.mc = Minecraft.getInstance();
    }

    public RadioGroup(int x, int y, RadioGroup.PressAction pressAction) {
        this.x = x;
        this.y = y;
        this.mc = Minecraft.getInstance();
        this.pressAction = pressAction;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int hoverIndex = 0;
        int hoveredRadio = -1;

        for (RadioGroup.RadioButton button : this.buttons.values()) {
            if (button.isOver(mouseX, mouseY)) {
                hoveredRadio = hoverIndex;
                break;
            }

            hoverIndex++;
        }

        int i = 0;

        for (RadioGroup.RadioButton button : this.buttons.values()) {
            RenderUtils.drawRect(button.x + 1, button.y, button.x + 7, button.y + 8, 1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawRect(button.x, button.y + 1, button.x + 8, button.y + 7, 1.0F, 1.0F, 1.0F, 1.0F);
            context.drawString(this.mc.font, button.label, button.x + 16, button.y, 16777215, false);
            if (button.selected) {
                RenderUtils.drawRect(button.x + 2, button.y + 1, button.x + 6, button.y + 7, 0.0F, 0.0F, 0.0F, 1.0F);
                RenderUtils.drawRect(button.x + 1, button.y + 2, button.x + 7, button.y + 6, 0.0F, 0.0F, 0.0F, 1.0F);
            }

            if (hoveredRadio == i && !button.selected) {
                RenderUtils.drawRect(button.x + 2, button.y + 1, button.x + 6, button.y + 7, 0.7F, 0.7F, 0.7F, 1.0F);
                RenderUtils.drawRect(button.x + 1, button.y + 2, button.x + 7, button.y + 6, 0.7F, 0.7F, 0.7F, 1.0F);
            }

            i++;
        }
    }

    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = 0;

        for (RadioGroup.RadioButton radioButton : this.buttons.values()) {
            if (radioButton.isOver(mouseX, mouseY)) {
                radioButton.selected = !radioButton.selected;
                if (this.pressAction != null) {
                    this.pressAction.onPress(this);
                }

                return true;
            }

            i++;
        }

        return false;
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }

    public void addRadioButton(int x, int y, String id, Component label) {
        this.buttons.put(id, new RadioGroup.RadioButton(this.x + x, this.y + y, label));
    }

    public ArrayList<String> getSelectedButtons() {
        ArrayList<String> selectedButtons = new ArrayList<>();

        for (String id : this.buttons.keySet()) {
            RadioGroup.RadioButton radioButton = this.buttons.get(id);
            if (radioButton.selected) {
                selectedButtons.add(id);
            }
        }

        return selectedButtons;
    }

    public void setRadioButton(String id, boolean value) {
        this.buttons.get(id).selected = value;
    }

    public interface PressAction {
        void onPress(RadioGroup var1);
    }

    private class RadioButton {
        public int x;
        public int y;
        public int width;
        public int height;
        public Component label;
        public boolean selected;

        public RadioButton(int x, int y, Component label) {
            this.x = x;
            this.y = y;
            this.width = 16 + RadioGroup.this.mc.font.width(label);
            this.height = 8;
            this.label = label;
        }

        public boolean isOver(double x, double y) {
            return x >= this.x
                && x <= this.x + this.width
                && y >= this.y
                && y <= this.y + this.height
                && !PlaylistClient.instance.playlistScreenInstance.isContextMenuActive();
        }
    }
}
