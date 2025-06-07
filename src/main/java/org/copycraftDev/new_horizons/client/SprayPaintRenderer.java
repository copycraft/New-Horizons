package org.copycraftDev.new_horizons.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import org.copycraftDev.new_horizons.Lidar.ColorUtils;
import org.copycraftDev.new_horizons.Lidar.SpraypaintScrollHandler;
import org.copycraftDev.new_horizons.core.items.custom.SprayPaintItem;

@Environment(EnvType.CLIENT)
public class SprayPaintRenderer implements BuiltinItemRenderer {
    private static final Identifier BASE_TEXTURE    = Identifier.of("new_horizons", "textures/item/spraypaint");
    private static final Identifier COLOR_TEXTURE   = Identifier.of("new_horizons", "textures/item/spraypaintOL");



    private void drawSprite(MatrixStack matrices,
                            VertexConsumerProvider provider,
                            Sprite sprite,
                            float r, float g, float b, float a,
                            int light,
                            int overlay) {
        VertexConsumer vc = provider.getBuffer(TexturedRenderLayers.getItemEntityTranslucentCull());
        MatrixStack.Entry entry = matrices.peek();

        // Quad from (0,0) to (1,1), tinted by (r,g,b,a)
        vc.vertex(entry.getPositionMatrix(), 0f, 0f, 0f)
                .color(r, g, b, a)
                .texture(sprite.getMinU(), sprite.getMaxV())
                .overlay(overlay)
                .light(light)
                .normal(0f, 0f, 1f)
                ;

        vc.vertex(entry.getPositionMatrix(), 1f, 0f, 0f)
                .color(r, g, b, a)
                .texture(sprite.getMaxU(), sprite.getMaxV())
                .overlay(overlay)
                .light(light)
                .normal(0f, 0f, 1f)
                ;

        vc.vertex(entry.getPositionMatrix(), 1f, 1f, 0f)
                .color(r, g, b, a)
                .texture(sprite.getMaxU(), sprite.getMinV())
                .overlay(overlay)
                .light(light)
                .normal(0f, 0f, 1f)
                ;

        vc.vertex(entry.getPositionMatrix(), 0f, 1f, 0f)
                .color(r, g, b, a)
                .texture(sprite.getMinU(), sprite.getMinV())
                .overlay(overlay)
                .light(light)
                .normal(0f, 0f, 1f)
                ;
    }

    @Override
    public void render(ItemStack itemStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int i1) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // Fabric 1.21.1: getSpriteAtlas returns Function<Identifier, Sprite>
        Function<Identifier, Sprite> atlas = mc.getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        Sprite baseSprite    = atlas.apply(BASE_TEXTURE);
        Sprite overlaySprite = atlas.apply(COLOR_TEXTURE);

        // 1) Draw the base layer (no tint)
        drawSprite(
                matrixStack,
                vertexConsumerProvider,
                baseSprite,
                1.0f, 1.0f, 1.0f, 1.0f,  // r, g, b, a = white/no-tint
                i,
                i1
        );

        // 2) Compute a hue-shifted RGB based on your ScrollHandler’s floats
        float[] shifted = ColorUtils.shiftHueFloat(
                SpraypaintScrollHandler.VALUE1,
                SpraypaintScrollHandler.VALUE2,
                SpraypaintScrollHandler.VALUE3,
                0f  // you can add an extra hue offset in degrees here if desired
        );

        // 3) Draw layer 1 tinted with shifted color
        drawSprite(
                matrixStack,
                vertexConsumerProvider,
                overlaySprite,
                shifted[0], shifted[1], shifted[2], 1.0f, // r, g, b, a
                i,
                i1
        );
    }
}
