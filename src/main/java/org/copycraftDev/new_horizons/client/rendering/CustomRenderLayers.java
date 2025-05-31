// CustomRenderLayers.java
package org.copycraftDev.new_horizons.client.rendering;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderPhase;

public class CustomRenderLayers {
    public static final RenderLayer ALWAYS_VISIBLE_LAYER = RenderLayer.of(
            "always_visible",
            VertexFormats.POSITION_COLOR, // or whatever format you need
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)  // always passes depth
                    .cull(RenderPhase.DISABLE_CULLING)         // disables face culling
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false)
    );
}
