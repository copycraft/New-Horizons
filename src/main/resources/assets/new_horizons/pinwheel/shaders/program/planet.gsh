#version 330 core
layout(points) in;
layout(triangle_strip, max_vertices = 64) out;

// Uniforms provided by the Veil engine:
uniform mat4 u_MVP;     // Model-View-Projection matrix
uniform float u_Radius; // Sphere radius (e.g., 0.5)

const int slices = 8;
const int stacks = 8;
const float PI = 3.14159265359;

void main() {
    // The input point serves as the sphere's center.
    vec4 center = gl_in[0].gl_Position;

    // Loop over stacks and slices to generate the sphere's vertices.
    for (int i = 0; i <= stacks; ++i) {
        float phi = PI * float(i) / float(stacks);
        for (int j = 0; j <= slices; ++j) {
            float theta = 2.0 * PI * float(j) / float(slices);
            // Compute the sphere's surface position in model space.
            vec3 pos = vec3(sin(phi) * cos(theta),
            cos(phi),
            sin(phi) * sin(theta));
            // Scale by the sphere's radius and add the center offset.
            vec4 vertexPos = center + vec4(pos * u_Radius, 0.0);
            // Transform to clip space.
            gl_Position = u_MVP * vertexPos;
            EmitVertex();
        }
        EndPrimitive();
    }
}
