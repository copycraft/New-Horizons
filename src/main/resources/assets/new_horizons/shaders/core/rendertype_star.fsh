#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec3 normal;

out vec4 fragColor;

void main() {


    vec4 color = texture(Sampler0, texCoord0);
    fragColor = color * 1.1;
}