package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.utils.GeneralUtils;
import com.fudgeu.playlist.utils.RenderUtils;
import com.fudgeu.playlist.utils.V3F;
import com.fudgeu.playlist.utils.VolumeBarTheme;
import com.fudgeu.playlist.utils.Wave;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class ExpandedVolumeControl implements GuiEventListener, Renderable, NarratableEntry {
    private final Minecraft mc;
    private final MusicManager musicManager;
    private final VolumeBarThemeManager volumeBarThemeManager;
    private final int x;
    private final int y;
    private float volume;
    private boolean visible = false;
    private float waveAnimationOffset = 0.0F;
    private boolean mouseDragging = false;
    private TransparentButton parentButton;
    private final ArrayList<V3F> waveValueLookup = new ArrayList<>();

    public ExpandedVolumeControl(int x, int y) {
        this.mc = Minecraft.getInstance();
        this.musicManager = PlaylistClient.instance.musicManager;
        this.volumeBarThemeManager = PlaylistClient.instance.volumeBarThemeManager;
        this.x = x;
        this.y = y - 120;
        this.volume = this.mc.options.getSoundSourceVolume(SoundSource.MUSIC);
        this.waveValueLookup.add(new V3F(1.0F, 4.0F, 3.0F));
        this.waveValueLookup.add(new V3F(1.5F, 8.0F, 4.0F));
        this.waveValueLookup.add(new V3F(1.5F, 5.0F, 3.0F));
        this.waveValueLookup.add(new V3F(1.5F, 3.0F, 4.0F));
        this.waveValueLookup.add(new V3F(1.5F, 4.0F, 5.0F));
        this.waveValueLookup.add(new V3F(1.5F, 8.0F, 6.0F));
        this.waveValueLookup.add(new V3F(1.5F, 7.0F, 3.0F));
        this.waveValueLookup.add(new V3F(1.5F, 3.0F, 6.0F));
    }

    public void setParentButton(TransparentButton parentButton) {
        this.parentButton = parentButton;
    }

    public void changeVolume(float newVolume) {
        this.mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set((double)newVolume);
        this.volume = newVolume;
        if (newVolume <= 0.0F) {
            this.musicManager.stopMusic();
        }
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            if ((mouseX < this.x - 11 || mouseX > this.x + 33 || mouseY < this.y || mouseY > this.y + 140) && !this.mouseDragging) {
                this.visible = false;
                this.parentButton.forceHover = false;
                return;
            }

            RenderUtils.drawRect(this.x - 1, this.y - 1, this.x + 21, this.y + 121, 0.5F, 0.5F, 0.5F, 1.0F);
            RenderUtils.drawRect(this.x, this.y, this.x + 20, this.y + 120, 0.1F, 0.1F, 0.1F, 1.0F);
            VolumeBarTheme theme = this.volumeBarThemeManager.getCurrentTheme();

            for (int i = this.x; i < this.x + 20; i++) {
                int j = 0;

                for (Wave wave : theme.waves) {
                    V3F values = this.waveValueLookup.get(j);
                    int height = 120;
                    RenderUtils.drawRect(
                        i,
                        Math.max(
                            this.y
                                + Math.round((1.0F - this.volume) * height)
                                - (int)(Math.sin((this.waveAnimationOffset * values.x + i) / values.y) * values.z)
                                + Math.round(wave.position * 100.0F * this.volume)
                                - 3,
                            this.y
                        ),
                        i + 1,
                        this.y + 120,
                        wave.color
                    );
                    j++;
                }
            }

            PoseStack matrices = context.pose();
            matrices.pushPose();
            float textScale = 0.666F;
            float positionScale = 1.0F / textScale;
            matrices.scale(textScale, textScale, textScale);
            String volumeString = Math.round(this.volume * 100.0F) + "%";
            Component volumeText = Component.literal(volumeString).withStyle(ChatFormatting.BOLD);
            float textSize = 9.7F - volumeString.length() * 2.0F;
            context.drawString(
                this.mc.font, volumeText, (int)((this.x + textSize) * positionScale), (int)((this.y - 8) * positionScale), 6710886, false
            );
            matrices.popPose();
            this.waveAnimationOffset -= delta;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.visible && mouseX >= this.x && mouseX <= this.x + 20 && mouseY >= this.y && mouseY <= this.y + 120) {
            this.volume = 1.0F - (float)(mouseY - this.y) / 120.0F;
            GeneralUtils.changeVolume(this.volume);
            this.mouseDragging = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseDragging) {
            this.mouseDragging = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.visible && mouseX >= this.x && mouseX <= this.x + 20 && mouseY >= this.y && mouseY <= this.y + 120) {
            this.volume = Math.max(Math.min(1.0F - (float)(mouseY - this.y) / 120.0F, 1.0F), 0.0F);
            GeneralUtils.changeVolume(this.volume);
            return true;
        } else if (this.visible && this.mouseDragging) {
            this.volume = Math.max(Math.min(1.0F - (float)(mouseY - this.y) / 120.0F, 1.0F), 0.0F);
            GeneralUtils.changeVolume(this.volume);
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

    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput builder) {
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
