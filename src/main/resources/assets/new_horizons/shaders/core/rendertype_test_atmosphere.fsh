#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec3 normal;
in vec2 screenCoord;


out vec4 fragColor;

void main() {
	vec2 uv = (screenCoord - 0.5) * 2.0;


	float d = max((1 * length(normal - vec3(0.,1.,0.))) - 6.0, 0.0);

    vec4 color = vec4(1.0, 1.0, 1.0, d);
    fragColor = color * ColorModulator;
}







































