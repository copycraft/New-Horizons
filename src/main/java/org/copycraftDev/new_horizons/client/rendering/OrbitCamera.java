package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OrbitCamera {
    public float rotX = 0f;
    public float rotY = 90f;
    public float zoom = 3.0f;
    public float panX = 0f, panY = 0f;

    private final Vector3f center       = new Vector3f(0f, 0f, 0f);
    private final Vector3f desiredCenter = new Vector3f(0f, 0f, 0f);

    // How fast the camera recenters on the target (units per second)
    private static final float CENTER_LERP_RATE = 5f;

    /**
     * Call whenever you want to switch focus.
     * The camera will then smoothly interpolate toward (x,y,z) over time.
     */
    public void setTarget(float x, float y, float z) {
        desiredCenter.set(x, y, z);
    }

    /** Instant zoom setting (e.g. from your zoom‐slider). */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /** Mouse‐drag delta → orbit rotation around the current center. */
    public void updateRotation(float dx, float dy) {
        rotY = (rotY + dx) % 360f;
        rotX = Math.max(-90f, Math.min(90f, rotX + dy));
    }

    /** Mouse‐drag delta → pan the camera in screen plane. */
    public void updatePan(float dx, float dy) {
        panX += dx;
        panY += dy;
    }

    /**
     * Must be called each frame, before apply().
     * @param deltaTime seconds since last frame.
     */
    public void tick(float deltaTime) {
        // Lerp center → desiredCenter
        float alpha = Math.min(1f, CENTER_LERP_RATE * deltaTime);
        center.lerp(desiredCenter, alpha);
    }

    /**
     * Applies the orbit camera transform to a MatrixStack
     * so all subsequent rendering is from this camera.
     */
    public void apply(MatrixStack matrices) {
        // 1) Pull back by zoom
        matrices.translate(0, 0, -zoom);
        // 2) Pan (scaled down to world coords)
        matrices.translate(panX * 0.1f, -panY * 0.1f, 0);
        // 3) Rotate
        matrices.multiply(new Quaternionf((float)Math.toRadians(rotX), 1, 0, 0));
        matrices.multiply(new Quaternionf((float)Math.toRadians(rotY), 0, 1, 0));
        // 4) Center
        matrices.translate(-center.x, -center.y, -center.z);
    }

    /**
     * Computes where the camera *would* be in world coordinates,
     * matching the logic in apply() so you can drive Minecraft's Camera.
     */
    public Vector3f getCameraPosition() {
        float ax = (float)Math.toRadians(rotX);
        float ay = (float)Math.toRadians(rotY);
        float cosX = (float)Math.cos(ax), sinX = (float)Math.sin(ax);
        float cosY = (float)Math.cos(ay), sinY = (float)Math.sin(ay);

        float cx = center.x + zoom * cosX * cosY;
        float cy = center.y + zoom * sinX;
        float cz = center.z + zoom * cosX * sinY;
        return new Vector3f(cx, cy, cz);
    }

    /**
     * Returns [yaw, pitch] for the in‐game Camera.
     */
    public float[] getCameraRotation() {
        return new float[]{ rotY, rotX };
    }
}
