package org.copycraftDev.new_horizons.extrastuff;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TextureResizer {

    public static Identifier resizeTexture(String namespace, String inputPath,
                                           int targetWidth, int targetHeight,
                                           String outputId) {
        Identifier inId = Identifier.of(namespace, inputPath);
        InputStream is;
        try {
            is = MinecraftClient.getInstance()
                    .getResourceManager()
                    .getResource(inId)
                    .orElseThrow()
                    .getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            BufferedImage original = ImageIO.read(is);
            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(original, 0, 0, targetWidth, targetHeight, null);
            g2.dispose();

            NativeImage nativeImg = new NativeImage(Format.RGBA, targetWidth, targetHeight, false);

            for (int y = 0; y < targetHeight; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    int argb = resized.getRGB(x, y);
                    int a    = (argb >> 24) & 0xFF;
                    int r    = (argb >> 16) & 0xFF;
                    int g    = (argb >>  8) & 0xFF;
                    int b    =  argb        & 0xFF;
                    int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                    nativeImg.setColor(x, y, abgr);
                }
            }

            Identifier outId = Identifier.of(namespace, outputId);
            NativeImageBackedTexture tex = new NativeImageBackedTexture(nativeImg);
            MinecraftClient.getInstance().getTextureManager().registerTexture(outId, tex);
            return outId;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
