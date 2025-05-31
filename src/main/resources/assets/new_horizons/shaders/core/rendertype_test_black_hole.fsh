#version 150

uniform sampler2D Sampler0;
uniform sampler2D DiffuseSampler;

uniform float Test;


in vec2 texCoord0;
in vec3 normal;
in vec2 screenCoord;
in float dist;
in vec3 that;
in vec3 lightDirection;

out vec4 fragColor;


void main() {
    // Convert screen coordinates (normally in [0,1] space) to a -1 to 1 range.
    vec2 uv = (screenCoord - 0.5) * 2.0;
    vec4 color = texture(DiffuseSampler, texCoord0);

    fragColor = vec4(Test);
}



















