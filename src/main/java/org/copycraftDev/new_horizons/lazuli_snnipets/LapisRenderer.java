package org.copycraftDev.new_horizons.lazuli_snnipets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import java.sql.Array;
import java.util.function.Supplier;
import java.util.List;

public class LapisRenderer {

    public static void setShader(Supplier<ShaderProgram> shader) {
        RenderSystem.setShader(shader);
    }

    public static void setShaderTexture(int slot, Identifier texture) {
        RenderSystem.setShaderTexture(slot, texture);
    }

    public static void disableBlend() {
        RenderSystem.disableBlend();
    }

    //overloaded method
    public static void setShaderColor(float r, float g, float b, float a) {RenderSystem.setShaderColor(r, g, b, a);}
    public static void setShaderColor(List<Integer> color) {RenderSystem.setShaderColor(color.get(0) / 255f, color.get(1) / 255f, color.get(2) / 255f, color.get(3) / 255f);}
    public static void setShaderColor(int[] color) {RenderSystem.setShaderColor(color[0] / 255f, color[1] / 255f, color[2] / 255f, color[3] / 255f);}

    public static void depthMask(boolean flag) {
        RenderSystem.depthMask(flag);
    }

    public static void setShaderFogShape(FogShape shape) {
        RenderSystem.setShaderFogShape(shape);
    }

    public static void setShaderFogColor(float r, float g, float b) {
        RenderSystem.setShaderFogColor(r, g, b);
    }

    public static void enableDepthTest() {
        RenderSystem.enableDepthTest();
    }

    public static void disableCull() {
        RenderSystem.disableCull();
    }

    public static void enableCull() {
        RenderSystem.enableCull();
    }

    public static void setShaderFogStart(float start) {
        RenderSystem.setShaderFogStart(start);
    }

    public static void setShaderFogEnd(float end) {
        RenderSystem.setShaderFogEnd(end);
    }

    public static void assertOnRenderThread() {}

    public static void disableDepthTest() {RenderSystem.disableDepthTest();}

    public static void setShader(ShaderProgram shader) {
        RenderSystem.setShader(() -> shader);
    }

    public static BufferBuilder drawAndReset(BufferBuilder buffer, Tessellator tessellator) {
        BuiltBuffer builtBuffer =  buffer.end();
        BufferRenderer.drawWithGlobalProgram(builtBuffer);
        return tessellator.begin(builtBuffer.getDrawParameters().mode(), builtBuffer.getDrawParameters().format());
    }


    public static void cleanupRenderSystem() {
        LapisRenderer.disableBlend();
        LapisRenderer.setShaderColor(1f,1f,1f,1f);
        LapisRenderer.depthMask(true);
        LapisRenderer.setShaderFogShape(FogShape.CYLINDER);
        LapisRenderer.setShaderFogColor(0f,0f,0f);
        LapisRenderer.setShader(GameRenderer::getPositionColorProgram);
        LapisRenderer.enableDepthTest();
    }

    public static void farAwayRendering() {
        LapisRenderer.setShaderFogStart(Integer.MAX_VALUE);
        LapisRenderer.setShaderFogEnd(Integer.MAX_VALUE);
    }



}
