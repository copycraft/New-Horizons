// wormhole.vsh
#version 330

// ------------
// Input Layout
// ------------
// "Position" is the built‐in attribute name for vertex positions in Veil/MC shaders.
// (See net.minecraft.client.render.Shader: modelViewMat, projectionMat uniforms: :contentReference[oaicite:2]{index=2})
// The input attribute is a vec3 for X/Y/Z.
in vec3 Position;

// ---------------
// Standard Uniforms
// ---------------
// Each Veil/MC shader pipeline provides these two matrices by default:
uniform mat4 modelViewMat;    // Transforms object‐space → eye/world‐space
uniform mat4 projectionMat;   // Transforms eye/world‐space → clip‐space

// --------------
// Outputs → Fragment
// --------------
// We want to know each fragment’s world‐space position (after modelViewMat).
// (So we can compute distance/angle from a given `Center` in the fragment shader.)
out vec3 worldPos;

void main() {
    // Apply Model‐View: moves our mesh from object‐space into eye/world‐space:
    vec4 mvPosition = modelViewMat * vec4(Position, 1.0);

    // Store the “world‐space” (eye‐space) position for the fragment:
    worldPos = mvPosition.xyz;

    // Finally, compute clip‐space position:
    gl_Position = projectionMat * mvPosition;
}
