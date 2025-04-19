#version 150

in vec2 vUv;
out vec4 FragColor;

// Bound by RenderSystem.setShaderTexture(0, ringTexture);
uniform sampler2D ringTexture;

// Set from Java: ringShader.getUniform("ringColor").set(r, g, b);
uniform vec3 ringColor;

// Set from Java: ringShader.getUniform("opacity").set(value);
uniform float opacity;

void main() {
    vec4 tex = texture(ringTexture, vUv);
    // Tint the ring and apply opacity
    FragColor = vec4(ringColor, 1.0) * tex;
    FragColor.a *= opacity;
    if (FragColor.a < 0.01) discard;
}
