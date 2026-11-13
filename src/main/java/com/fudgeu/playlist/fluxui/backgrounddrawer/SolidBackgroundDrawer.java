package com.fudgeu.playlist.fluxui.backgrounddrawer;

import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.ColorStyle;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import org.joml.Matrix4f;

public class SolidBackgroundDrawer implements BackgroundDrawer {
    private final HashMap<Integer, int[]> cachedCircles = new HashMap<>();

    @Override
    public void draw(GuiGraphics context, int x, int y, int width, int height, InheritedStyles inheritedStyles, ColorStyle colorStyle) {
        Color color = colorStyle.getColor();
        int endX = x + width;
        int endY = y + height;
        int borderRadius = inheritedStyles.getBorderRadius();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer;
        Matrix4f posMatrix = context.pose().last().pose();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * inheritedStyles.getOpacity());
        buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.addVertex(posMatrix, (float) (x), (float) (endY - borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX), (float) (endY - borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX), (float) (y + borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (x), (float) (y + borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (x + borderRadius), (float) (y + borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX - borderRadius), (float) (y + borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX - borderRadius), (float) (y), 0.0F);
        buffer.addVertex(posMatrix, (float) (x + borderRadius), (float) (y), 0.0F);
        buffer.addVertex(posMatrix, (float) (x + borderRadius), (float) (endY), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX - borderRadius), (float) (endY), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX - borderRadius), (float) (endY - borderRadius), 0.0F);
        buffer.addVertex(posMatrix, (float) (x + borderRadius), (float) (endY - borderRadius), 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        int[] circleMatrix = this.getCircleMatrix(borderRadius);
        int rX = endX - borderRadius;
        int lX = x + borderRadius - 1;
        int bY = endY - borderRadius;
        int tY = y + borderRadius;

        for (int x1 = 0; x1 < circleMatrix.length; x1++) {
            buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
            int l = circleMatrix[x1];
            buffer.addVertex(posMatrix, (float) (rX + x1), (float) (bY + l), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1 + 1), (float) (bY + l), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1 + 1), (float) (bY), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1), (float) (bY), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1), (float) (bY + l), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1 + 1), (float) (bY + l), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1 + 1), (float) (bY), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1), (float) (bY), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1), (float) (tY), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1 + 1), (float) (tY), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1 + 1), (float) (tY - l), 0.0F);
            buffer.addVertex(posMatrix, (float) (lX - x1), (float) (tY - l), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1), (float) (tY), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1 + 1), (float) (tY), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1 + 1), (float) (tY - l), 0.0F);
            buffer.addVertex(posMatrix, (float) (rX + x1), (float) (tY - l), 0.0F);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    public int[] getCircleMatrix(int r) {
        if (this.cachedCircles.containsKey(r)) {
            return this.cachedCircles.get(r);
        } else {
            int[] resultMatrix = new int[r];

            for (int x = 0; x < r; x++) {
                resultMatrix[x] = (int)Math.ceil(Math.sqrt(1.0 - Math.pow((double)(x + 1) / r, 2.0)) * r - 1.0);
            }

            this.cachedCircles.put(r, resultMatrix);
            return resultMatrix;
        }
    }
}
