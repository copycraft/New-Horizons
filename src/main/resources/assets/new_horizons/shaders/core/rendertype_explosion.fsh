#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec3 normal;
in vec2 screenCoord;
in float dist;
in vec3 that;
in vec3 lightDirection;


out vec4 fragColor;

void main() {
	vec2 uv = (screenCoord - 0.5) * 2.0;


	float d = dist-0.5;

    vec4 color = vec4(0.3+d,0.35+d,1.0+d,d);
    
    color = vec4(0.7);
    
    fragColor = color * ColorModulator;
}
















