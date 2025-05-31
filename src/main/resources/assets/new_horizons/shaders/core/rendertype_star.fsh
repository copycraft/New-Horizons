#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec3 normal;

out vec4 fragColor;

void main() {


    vec4 color = texture(Sampler0, texCoord0);
    fragColor = color * 1.1;
}
