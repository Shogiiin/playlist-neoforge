package com.fudgeu.playlist;

import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ToastManager {
    private final Minecraft mc;
    private final ArrayList<ToastManager.Toast> toasts = new ArrayList<>();
    private int yOffset = 0;

    public ToastManager() {
        this.mc = Minecraft.getInstance();
    }

    public void tick() {
        ArrayList<Integer> toRemove = new ArrayList<>();

        for (int i = 0; i < this.toasts.size(); i++) {
            this.toasts.get(i).timeRemaining--;
            if (this.toasts.get(i).timeRemaining <= 0) {
                toRemove.add(i);
            }
        }

        for (int ix : toRemove) {
            this.toasts.remove(ix);
        }
    }

    public void render(GuiGraphics context, int width, int height) {
        int i = this.toasts.size();

        for (ToastManager.Toast toast : this.toasts) {
            int toastX = width - toast.textLength - 30;
            int toastEndX = width - 10;
            int toastY = height - 40 * i - this.yOffset;
            int toastEndY = height - 40 * i + 30 - this.yOffset;
            int toastTimerEndX = (int)Math.ceil(toastEndX - (1.0F - (float)toast.timeRemaining / toast.displayTime) * (toastEndX - toastX));
            RenderUtils.drawRect(toastX, toastY, toastEndX, toastEndY, 0.05F, 0.05F, 0.05F, 1.0F);
            RenderUtils.drawRect(toastX, toastEndY - 2, toastTimerEndX, toastEndY, 0.1F, 0.1F, 0.1F, 1.0F);
            context.drawString(this.mc.font, toast.text, toastX + 10, toastY + 11, 16777215, false);
            i--;
        }
    }

    public void addToast(Component text, int displayTime) {
        this.toasts.add(new ToastManager.Toast(text, displayTime));
        if (this.toasts.size() == 4) {
            this.toasts.remove(0);
        }
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    class Toast {
        public Component text;
        public int displayTime;
        public int timeRemaining;
        public int textLength;

        public Toast(Component text, int displayTime) {
            this.text = text;
            this.displayTime = displayTime;
            this.timeRemaining = displayTime;
            this.textLength = ToastManager.this.mc.font.width(text);
        }
    }
}
