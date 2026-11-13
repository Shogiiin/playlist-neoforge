package com.fudgeu.playlist.gui;

import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.enums.SortCategory;
import com.fudgeu.playlist.enums.SortDirection;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;

public class SortButton extends Button {
    private Minecraft mc = Minecraft.getInstance();
    private ResourceLocation arrowUpTexture;
    private ResourceLocation arrowDownTexture;
    private int messageWidth;
    private PlaylistScreen playlistScreen;
    private SortCategory sortCategory;

    public SortButton(PlaylistScreen playlistScreen, int x, int y, Component message, SortCategory sortCategory, OnPress onPress) {
        super(x, y, Minecraft.getInstance().font.width(message) + 12, 9, message, onPress, DEFAULT_NARRATION);
        TextureManager textureManager = PlaylistClient.instance.textureManager;
        this.messageWidth = this.mc.font.width(message);
        this.playlistScreen = playlistScreen;
        this.sortCategory = sortCategory;
        this.arrowDownTexture = textureManager.getTexture("arrow_down");
        this.arrowUpTexture = textureManager.getTexture("arrow_up");
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        int x = this.getX();
        int y = this.getY();
        this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
        if (this.visible) {
            float brightnessMultiplier = 0.4F;
            if (this.isHovered()) {
                brightnessMultiplier = 0.6F;
            }

            context.drawString(this.mc.font, this.getMessage(), x, y, (int)(1.6777215E7F * brightnessMultiplier), false);
            if (this.playlistScreen.sortQuery.getCategory() == this.sortCategory
                || this.sortCategory == SortCategory.TITLE && this.playlistScreen.sortQuery.getCategory() == SortCategory.ARTIST) {
                context.setColor(1.0F * brightnessMultiplier, 1.0F * brightnessMultiplier, 1.0F * brightnessMultiplier, 1.0F);
                if (this.playlistScreen.sortQuery.getDirection() == SortDirection.ASCENDING) {
                    context.blit(this.arrowDownTexture, x + this.messageWidth + 3, y, 0.0F, 0.0F, 8, 8, 8, 8);
                } else {
                    context.blit(this.arrowUpTexture, x + this.messageWidth + 3, y, 0.0F, 0.0F, 8, 8, 8, 8);
                }

                context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void updateMessage() {
        if (this.sortCategory == SortCategory.TITLE) {
            if (this.playlistScreen.sortQuery.getCategory() == SortCategory.TITLE && !Objects.equals(this.getMessage().getString(), "Title")) {
                this.setMessage(Component.translatable("generic.title").withStyle(ChatFormatting.ITALIC));
                this.messageWidth = this.mc.font.width(Component.translatable("generic.title").withStyle(ChatFormatting.ITALIC).getString());
                this.setWidth(this.messageWidth + 12);
            } else if (this.playlistScreen.sortQuery.getCategory() == SortCategory.ARTIST && !Objects.equals(this.getMessage().getString(), "Artist")) {
                this.setMessage(Component.translatable("generic.artist").withStyle(ChatFormatting.ITALIC));
                this.messageWidth = this.mc.font.width(Component.translatable("generic.artist").withStyle(ChatFormatting.ITALIC).getString());
                this.setWidth(this.messageWidth + 12);
            } else {
                this.setMessage(Component.translatable("generic.title").withStyle(ChatFormatting.ITALIC));
                this.messageWidth = this.mc.font.width(Component.translatable("generic.title").withStyle(ChatFormatting.ITALIC).getString());
                this.setWidth(this.messageWidth + 12);
            }
        }
    }
}
