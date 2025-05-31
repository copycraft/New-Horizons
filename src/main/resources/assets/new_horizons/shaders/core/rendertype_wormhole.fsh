// wormhole.fsh
#version 330

// ------------------
// Inputs from Vertex
// ------------------
in vec3 worldPos;      // Interpolated world‐space coordinate (from wormhole.vsh)

// ------------------
// Our Custom Uniforms
// ------------------
// (Veil will automatically bind these if we set them from Java each frame.)
// "Time" and "Center" must match the uniform names we update via Veil API.
uniform float Time;    // Elapsed time (in seconds or ticks) → controls rotation speed.
uniform vec3 Center;   // Center of the wormhole in world‐space (x, y, z).

// ------------------
// Output to Framebuffer
// ------------------
out vec4 fragColor;

void main() {
    // 1. Compute the 2D direction from Center on the XZ plane (ignore y for radial effect):
    vec2 dir = worldPos.xz - Center.xz;
    float dist = length(dir);
    float radius = 2.0;           // Wormhole radius (in world units)
    if (dist > radius) {
        // Outside the circle → fully discard (creates a hard circular cutout)
        discard;
    }

    // 2. Normalize radial coordinate (0.0 at center → 1.0 at edge):
    float norm = dist / radius;

    // 3. Compute a swirling angle: base angle + time-driven rotation:
    float baseAngle = atan(dir.y, dir.x);      // atan2(dirY, dirX) in GLSL is atan(y, x)
    float swirlSpeed = 1.5;                    // How fast the vortex spins
    float swirl = baseAngle + Time * swirlSpeed;

    // 4. Use `swirl` to generate a color wave:
    float wave = sin(swirl * 4.0 + Time * 2.0) * 0.5 + 0.5;
    // ‣ Multiply swirl by 4.0 to get 4 “arms” in the vortex;
    // ‣ + Time*2.0 ensures continual animation.

    // 5. Fade out near the rim (radial fade):
    float fade = 1.0 - norm;   // 1.0 at center, 0.0 at edge

    // 6. Compose final color (purple→cyan gradient, modulated by fade):
    vec3 color = vec3(wave * 0.7 + 0.3, 0.2 + 0.5 * (1.0 - wave), 1.0 - wave) * fade;

    // 7. Alpha reduction near the edge (makes it look “transparent” at outer rim):
    float alpha = fade * 0.8;

    fragColor = vec4(color, alpha);
}
