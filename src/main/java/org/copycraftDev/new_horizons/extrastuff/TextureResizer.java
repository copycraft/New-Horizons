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
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TextureResizer {

    private static final Random RANDOM = new Random();

    public record ResizeJob(String inputPath, String outputId, int width, int height, boolean applyNoise) {}

    /**
     * Batch resize multiple textures.
     *
     * @param namespace  The mod namespace
     * @param jobs       A list of resize jobs to execute
     * @return A list of Identifiers of the newly registered textures
     */
    public static List<Identifier> resizeTextures(String namespace, List<ResizeJob> jobs) {
        return jobs.stream()
                .map(job -> resizeTexture(namespace, job.inputPath(), job.width(), job.height(), job.outputId(), job.applyNoise()))
                .filter(id -> id != null)
                .toList();
    }

    /**
     * Resize a single texture and register it.
     */
    public static Identifier resizeTexture(String namespace, String inputPath,
                                           int targetWidth, int targetHeight,
                                           String outputId, boolean applyNoise) {

        Identifier inId = Identifier.of(namespace, inputPath);
        try {
            Optional<Resource> resourceOpt = MinecraftClient.getInstance()
                    .getResourceManager()
                    .getResource(inId);

            if (resourceOpt.isEmpty()) {
                throw new IOException("Texture not found: " + inId);
            }

            BufferedImage original = javax.imageio.ImageIO.read(resourceOpt.get().getInputStream());
            BufferedImage resized = resizeBufferedImage(original, targetWidth, targetHeight);

            if (applyNoise) {
                applyNoise(resized);
            }

            NativeImage nativeImage = convertToNativeImage(resized);
            String uniqueOutputId = outputId + "_" + System.currentTimeMillis();
            Identifier outId = Identifier.of(namespace, uniqueOutputId);

            MinecraftClient.getInstance().getTextureManager()
                    .registerTexture(outId, new NativeImageBackedTexture(nativeImage));

            return outId;

        } catch (IOException e) {
            System.err.println("Error resizing texture: " + inId);
            e.printStackTrace();
            return null;
        }
    }

    private static BufferedImage resizeBufferedImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return resized;
    }

    private static NativeImage convertToNativeImage(BufferedImage img) {
        NativeImage nativeImg = new NativeImage(NativeImage.Format.RGBA, img.getWidth(), img.getHeight(), false);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                nativeImg.setColor(x, y, abgr);
            }
        }
        return nativeImg;
    }

    private static void applyNoise(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int noise = RANDOM.nextInt(50) - 25;
                Color c = new Color(image.getRGB(x, y), true);
                Color newColor = new Color(
                        clamp(c.getRed() + noise),
                        clamp(c.getGreen() + noise),
                        clamp(c.getBlue() + noise),
                        c.getAlpha()
                );
                image.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}
