#version 150

// Vertex attributes
in vec3 Position;
in vec2 UV;

// Passed to the fragment shader
out vec2 vUv;

// Built‑in uniforms provided by Minecraft’s ShaderProgram wrapper
uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main() {
    vUv = UV;
    gl_Position = projection * view * model * vec4(Position, 1.0);
}
