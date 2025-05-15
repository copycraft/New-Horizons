package org.copycraftDev.new_horizons.extrastuff;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Random;

public class TextureResizer {

    private static final Random RANDOM = new Random();

    /**
     * Resize a texture, optionally adding random noise, and then register it as a dynamic texture.
     *
     * @param namespace    Your mod ID
     * @param inputPath    Path to the texture to resize
     * @param targetWidth  Target width for the resized texture
     * @param targetHeight Target height for the resized texture
     * @param outputId     The base output identifier for the resized texture
     * @param applyNoise   Whether to apply noise to the texture
     * @return The unique identifier of the resized texture
     */
    public static Identifier resizeTexture(String namespace, String inputPath,
                                           int targetWidth, int targetHeight,
                                           String outputId, boolean applyNoise) {
        // Load the original texture
        Identifier inId = Identifier.of(namespace, inputPath);
        Optional<Resource> resourceStreamOpt;

        try {
            resourceStreamOpt = MinecraftClient.getInstance()
                    .getResourceManager()
                    .getResource(inId);

            if (resourceStreamOpt.isEmpty()) {
                throw new IOException("Texture resource not found: " + inId.toString());
            }

            InputStream is = resourceStreamOpt.get().getInputStream();
            BufferedImage original = javax.imageio.ImageIO.read(is);
            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(original, 0, 0, targetWidth, targetHeight, null);
            g2.dispose();

            // Apply random noise if the flag is true
            if (applyNoise) {
                applyNoise(resized);
            }

            // Convert BufferedImage to NativeImage
            NativeImage nativeImg = new NativeImage(NativeImage.Format.RGBA, targetWidth, targetHeight, false);
            for (int y = 0; y < targetHeight; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    int argb = resized.getRGB(x, y);
                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                    nativeImg.setColor(x, y, abgr);
                }
            }

            // Generate a unique output identifier
            String uniqueOutputId = outputId + "_" + System.currentTimeMillis();
            Identifier outId = Identifier.of(namespace, uniqueOutputId);

            // Register the resized image as a dynamic texture
            NativeImageBackedTexture tex = new NativeImageBackedTexture(nativeImg);
            MinecraftClient.getInstance().getTextureManager().registerTexture(outId, tex);
            return outId;

        } catch (IOException e) {
            // Log more details to help with debugging
            System.err.println("Error loading or resizing texture: " + inId);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds random noise to a BufferedImage (used for texture distortion).
     *
     * @param image The image to apply noise to
     */
    private static void applyNoise(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int noise = RANDOM.nextInt(50) - 25;  // Random noise between -25 and 25
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);

                // Modify color values based on noise
                int r = Math.min(255, Math.max(0, color.getRed() + noise));
                int g = Math.min(255, Math.max(0, color.getGreen() + noise));
                int b = Math.min(255, Math.max(0, color.getBlue() + noise));

                Color newColor = new Color(r, g, b, color.getAlpha());
                image.setRGB(x, y, newColor.getRGB());
            }
        }
    }
}
