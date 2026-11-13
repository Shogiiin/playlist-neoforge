package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.utils.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager.LogicOp;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

public class TransparentTextField extends EditBox {
    private Font textRenderer;
    private Component label;
    private boolean isEditable = true;
    private int labelColor = 11579568;
    private int editableColor = 16777215;
    private int uneditableColor = 11579568;
    private int lineScrollOffset;
    private int selectionEnd;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private int maxLength = 64;
    private ResourceLocation iconPath;

    public TransparentTextField(Font textRenderer, int x, int y, int width, int height, Component label) {
        super(textRenderer, x, y, width, height, label);
        this.textRenderer = textRenderer;
        this.label = label;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.isVisible()) {
            int x = this.getX();
            int y = this.getY();
            RenderUtils.drawRect(x, y, x + this.width, y + this.height, 0.1F, 0.1F, 0.1F, 1.0F);
            boolean showLabel = !this.isFocused() && this.getValue().isEmpty();
            int textColor = showLabel ? this.labelColor : (this.isEditable ? this.editableColor : this.uneditableColor);
            float r = (textColor >> 16 & 0xFF) / 255.0F;
            float g = (textColor >> 8 & 0xFF) / 255.0F;
            float b = (textColor & 0xFF) / 255.0F;
            int cursorLocation = this.getCursorPosition() - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String text = showLabel ? this.label.getString() : this.getValue();
            String cutText = this.textRenderer.plainSubstrByWidth(text.substring(this.lineScrollOffset), this.getWidth());
            boolean renderCursor = cursorLocation >= 0 && cursorLocation <= cutText.length();
            boolean flag1 = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && renderCursor;
            int iconOffset = this.iconPath != null ? 20 : 0;
            int textX = (this.enableBackgroundDrawing ? x + 4 : x) + iconOffset;
            int textY = this.enableBackgroundDrawing ? y + (this.height - 8) / 2 : y;
            if (this.iconPath != null) {
                context.setColor(r, g, b, 1.0F);
                context.blit(this.iconPath, textX - 16, textY, 0.0F, 0.0F, 8, 8, 8, 8);
                context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            int j1 = textX;
            if (k > cutText.length()) {
                k = cutText.length();
            }

            if (!cutText.isEmpty()) {
                String fixedString = renderCursor ? cutText.substring(0, cursorLocation) : cutText;
                Component fixedText;
                if (showLabel) {
                    fixedText = Component.literal(fixedString).withStyle(ChatFormatting.ITALIC);
                } else {
                    fixedText = Component.literal(fixedString);
                }

                j1 = context.drawString(this.textRenderer, fixedText, textX, textY, textColor, true);
            }

            boolean flag2 = this.getCursorPosition() < this.getValue().length() || this.getValue().length() >= this.maxLength;
            int k1 = j1;
            if (!renderCursor) {
                k1 = cursorLocation > 0 ? textX + this.width : textX;
            } else if (flag2) {
                k1 = j1 - 1;
                j1--;
            }

            if (!cutText.isEmpty() && renderCursor && cursorLocation < cutText.length()) {
                String fixedString = cutText.substring(cursorLocation);
                Component fixedText;
                if (showLabel) {
                    fixedText = Component.literal(fixedString).withStyle(ChatFormatting.ITALIC);
                } else {
                    fixedText = Component.literal(fixedString);
                }

                context.drawString(this.textRenderer, fixedText, j1, textY, textColor, true);
            }

            if (flag1) {
                if (flag2) {
                    RenderUtils.drawRect(k1, textY - 1, k1, textY + 1 + 9, 0.0F, 0.0F, 0.0F, 0.4F);
                } else {
                    context.drawString(this.textRenderer, "|", k1, textY, textColor, true);
                }
            }

            if (k != cursorLocation) {
                int l1 = textX + this.textRenderer.width(cutText.substring(0, k));
                this.drawSelectionBox(k1, textY - 1, l1 - 1, textY + 1 + 9);
            }
        }
    }

    // EditBox.tick() was removed in 1.20.2 (cursor blink is time-based now); kept for the custom cursor counter.
    public void tick() {
        this.cursorCounter++;
    }

    public void setHighlightPos(int position) {
        super.setHighlightPos(position);
        int i = this.getValue().length();
        this.selectionEnd = Mth.clamp(position, 0, i);
        if (this.textRenderer != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }

            int j = this.getInnerWidth();
            String s = this.textRenderer.plainSubstrByWidth(this.getValue().substring(this.lineScrollOffset), j, false);
            int k = s.length() + this.lineScrollOffset;
            if (this.selectionEnd == this.lineScrollOffset) {
                this.lineScrollOffset = this.lineScrollOffset - this.textRenderer.plainSubstrByWidth(this.getValue(), j, true).length();
            }

            if (this.selectionEnd > k) {
                this.lineScrollOffset = this.lineScrollOffset + (this.selectionEnd - k);
            } else if (this.selectionEnd <= this.lineScrollOffset) {
                this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - this.selectionEnd);
            }

            this.lineScrollOffset = Mth.clamp(this.lineScrollOffset, 0, i);
        }
    }

    private void drawSelectionBox(int startX, int startY, int endX, int endY) {
        int x = this.getX();
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > x + this.width) {
            endX = x + this.width;
        }

        if (startX > x + this.width) {
            startX = x + this.width;
        }

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(LogicOp.OR_REVERSE);
        bufferbuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex((float) (startX), (float) (endY), (float) (0.0));
        bufferbuilder.addVertex((float) (endX), (float) (endY), (float) (0.0));
        bufferbuilder.addVertex((float) (endX), (float) (startY), (float) (0.0));
        bufferbuilder.addVertex((float) (startX), (float) (startY), (float) (0.0));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableColorLogicOp();
    }

    public void setIcon(ResourceLocation iconPath) {
        this.iconPath = iconPath;
    }

    public void setLabel(Component label) {
        this.label = label;
    }
}
