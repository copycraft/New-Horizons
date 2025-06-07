package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.lazuli_snnipets.LapisRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliRenderingRegistry;

public class SniperHudRenderer {
    public static boolean renderHud = false;
    public static double charge = 0;

    // Base bar config
    private static final int BAR_X      = 10;
    private static final int BAR_Y      = 50;
    private static final int BAR_WIDTH  = 120;
    private static final int SEGMENTS   = 6;

    // Segment size & spacing
    private static final int SEGMENT_HEIGHT  = 6;  // slightly shorter than original bar
    private static final int SEGMENT_SPACING = 20; // vertical space between segments

    // Colors (ARGB)
    private static final int COLOR_BG       = 0x80000000;
    private static final int COLOR_DARKGRAY = 0xFF222222;
    private static final int COLOR_DARKBLUE = 0xFF001144;
    private static final int COLOR_BLUE1    = 0xFF335588;
    private static final int COLOR_BLUE2    = 0xFF6699BB;
    private static final int COLOR_BLUE3    = 0xFF99CCDD;
    private static final int COLOR_CYAN     = 0xFF00FFFF;

    // Shader placeholder
    private static ShaderProgram TEST_SHADER;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!renderHud) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            TextRenderer textRenderer = mc.textRenderer;

            // Draw charging text
            float percent = MathHelper.clamp((float)(charge / 1.2), 0f, 100f);
            String chargeText = String.format("Charging... §3%.0f%%", percent);
            drawContext.drawText(textRenderer, chargeText, BAR_X, BAR_Y - 16, 0xFFFFFF, true);

            // Compute fill ratio [0..1]
            float norm = MathHelper.clamp((float)(charge / 120.0), 0f, 1f);
            float totalFill = norm * SEGMENTS;
            int fullSegments = (int)Math.floor(totalFill);
            float partialFill = totalFill - fullSegments;

            // Color steps
            int[] stepColors = new int[] {
                    COLOR_DARKGRAY,
                    COLOR_DARKBLUE,
                    COLOR_BLUE1,
                    COLOR_BLUE2,
                    COLOR_BLUE3,
                    COLOR_CYAN
            };
            int colorIndex = Math.min(fullSegments, stepColors.length - 1);
            int fillColor = stepColors[colorIndex];

            // Draw each segment spread out vertically
            for (int i = 0; i < SEGMENTS; i++) {
                int yStart = BAR_Y + i * (SEGMENT_HEIGHT + SEGMENT_SPACING);

                // Determine fill width
                int fillWidth;
                if (i < fullSegments) {
                    fillWidth = BAR_WIDTH;
                } else if (i == fullSegments) {
                    fillWidth = (int)(BAR_WIDTH * partialFill);
                } else {
                    fillWidth = 0;
                }

                // Draw filled portion
                if (fillWidth > 0) {
                    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    drawContext.fillGradient(
                            BAR_X, yStart,
                            BAR_X + fillWidth, yStart + SEGMENT_HEIGHT,
                            fillColor,
                            fillColor);
                    RenderSystem.disableBlend();
                }

                // Draw background for empty portion
                if (fillWidth < BAR_WIDTH) {
                    drawContext.fill(
                            BAR_X + fillWidth, yStart,
                            BAR_X + BAR_WIDTH, yStart + SEGMENT_HEIGHT,
                            COLOR_BG);
                }
            }
        });

        // Existing 3D rendering (unchanged)
        LazuliRenderingRegistry.register((context, viewProjMatrix, tickDelta) -> {
            Camera camera = context.camera();

            LapisRenderer.farAwayRendering();
            LapisRenderer.disableCull();
            LapisRenderer.enableDepthTest();
            RenderSystem.setShaderFogColor(0, 0, 0, 0);

            Tessellator tessellator = Tessellator.getInstance();
            var bb = tessellator.begin(
                    VertexFormat.DrawMode.QUADS,
                    VertexFormats.POSITION_TEXTURE_COLOR_NORMAL
            );

            TEST_SHADER = GameRenderer.getPositionColorProgram();
            LapisRenderer.setShaderColor(1, 0, 0, 1);
            LapisRenderer.setShader(TEST_SHADER);

            LazuliGeometryBuilder.buildTexturedSphere(
                    20,
                    10f,
                    new Vec3d(300, 100, 0),
                    new Vec3d(0, 1, 0),
                    0,
                    false,
                    camera,
                    viewProjMatrix,
                    bb
            );

            bb = LapisRenderer.drawAndReset(bb, tessellator);
            LapisRenderer.cleanupRenderSystem();
            LapisRenderer.cleanupRenderSystem();
        });
    }
}