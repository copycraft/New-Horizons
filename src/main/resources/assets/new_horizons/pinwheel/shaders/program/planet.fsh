#version 450 core
out vec4 FragColor;
in vec3 fragNormal;

void main() {
    vec3 color = vec3(1.0); // White color
    FragColor = vec4(color, 1.0);
}
