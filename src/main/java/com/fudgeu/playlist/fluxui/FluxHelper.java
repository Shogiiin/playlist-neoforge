package com.fudgeu.playlist.fluxui;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import org.joml.Matrix4f;

public class FluxHelper {
    public static boolean isInside(int x, int y, int elementX, int elementY, int elementWidth, int elementHeight) {
        int endX = elementX + elementWidth;
        int endY = elementY + elementHeight;
        return x >= elementX && y >= elementY && x <= endX && y <= endY;
    }

    public static float mixOpacity(float opacity1, float opacity2) {
        return opacity1 * opacity2;
    }

    public static void drawRectangle(GuiGraphics context, int x, int y, int width, int height, float r, float g, float b, float a) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer;
        Matrix4f posMatrix = context.pose().last().pose();
        int endX = x + width;
        int endY = y + height;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);
        buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.addVertex(posMatrix, (float) (x), (float) (endY), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX), (float) (endY), 0.0F);
        buffer.addVertex(posMatrix, (float) (endX), (float) (y), 0.0F);
        buffer.addVertex(posMatrix, (float) (x), (float) (y), 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
