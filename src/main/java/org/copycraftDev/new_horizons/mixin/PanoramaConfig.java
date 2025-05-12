package org.copycraftDev.new_horizons.mixin;

import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRendererPanorama; /**
 * Immutable configuration for panorama rendering transforms.
 */
public record PanoramaConfig(
                float rotationX, float rotationY, float rotationZ,
                float scale,
                float offsetX, float offsetY,
                float cameraX, float cameraY, float cameraZ,
                float planetZ
        ) {
    public static Builder builder() { return new Builder(); }

    /** Applies this config to the static renderer. */
    public void applyTo(Class<CelestialBodyRendererPanorama> ignored) {
        CelestialBodyRendererPanorama.setRotationX(rotationX);
        CelestialBodyRendererPanorama.setRotationY(rotationY);
        CelestialBodyRendererPanorama.setRotationZ(rotationZ);
        CelestialBodyRendererPanorama.setScale(scale);
        CelestialBodyRendererPanorama.setOffsetX(offsetX);
        CelestialBodyRendererPanorama.setOffsetY(offsetY);
        CelestialBodyRendererPanorama.setCameraOffset(cameraX, cameraY, cameraZ);
        CelestialBodyRendererPanorama.setPlanetZ(planetZ);
    }

    public PanoramaConfig copy() { return this; }

    public void update(PanoramaConfig config) {
    }

    public static class Builder {
        private float rotationX, rotationY, rotationZ;
        private float scale = 1f;
        private float offsetX, offsetY;
        private float cameraX, cameraY, cameraZ;
        private float planetZ;

        public Builder rotation(float x, float y, float z) {
            this.rotationX = x; this.rotationY = y; this.rotationZ = z; return this;
        }
        public Builder scale(float s) { this.scale = s; return this; }
        public Builder offset(float x, float y) { this.offsetX = x; this.offsetY = y; return this; }
        public Builder cameraOffset(float x, float y, float z) { this.cameraX = x; this.cameraY = y; this.cameraZ = z; return this; }
        public Builder planetBaseZ(float z) { this.planetZ = z; return this; }
        public PanoramaConfig build() {
            return new PanoramaConfig(rotationX, rotationY, rotationZ, scale, offsetX, offsetY, cameraX, cameraY, cameraZ, planetZ);
        }
    }
}