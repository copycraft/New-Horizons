#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(DiffuseSampler, texCoord);

    vec3 OutColor = vec3(1.0,0.0,0.0);

    fragColor = vec4(OutColor, 1.0);
}
