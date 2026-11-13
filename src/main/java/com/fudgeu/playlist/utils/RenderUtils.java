package com.fudgeu.playlist.utils;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

public class RenderUtils {
    public static void drawRect(int x, int y, int endX, int endY, float r, float g, float b, float a) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);
        buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.addVertex((float) (x), (float) (endY), (float) (0.0));
        buffer.addVertex((float) (endX), (float) (endY), (float) (0.0));
        buffer.addVertex((float) (endX), (float) (y), (float) (0.0));
        buffer.addVertex((float) (x), (float) (y), (float) (0.0));
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawRect(int x, int y, int endX, int endY, RGBA color) {
        drawRect(x, y, endX, endY, color.r, color.g, color.b, color.a);
    }

    public static void drawRectGradient(int x, int y, int endX, int endY, RGBA color1, RGBA color2) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        for (int i = x; i < endX; i++) {
            for (int j = y; j < endY; j++) {
                float rGap = color1.r - color2.r;
                float gGap = color1.g - color2.g;
                float bGap = color1.b - color2.b;
                float yPos = (float)(j - y) / (endY - y);
                float shaderR = color2.r + yPos * rGap;
                float shaderG = color2.g + yPos * gGap;
                float shaderB = color2.b + yPos * bGap;
                RenderSystem.setShaderColor(shaderR, shaderG, shaderB, 1.0F);
                buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
                buffer.addVertex((float) (i), (float) (j + 1.0), (float) (0.0));
                buffer.addVertex((float) (i + 1.0), (float) (j + 1.0), (float) (0.0));
                buffer.addVertex((float) (i + 1.0), (float) (j), (float) (0.0));
                buffer.addVertex((float) (i), (float) (j), (float) (0.0));
                BufferUploader.drawWithShader(buffer.buildOrThrow());
            }
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
