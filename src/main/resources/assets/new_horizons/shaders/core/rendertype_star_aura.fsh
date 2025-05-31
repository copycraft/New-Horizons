#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform float gameTime;


in vec2 texCoord0;
in vec3 normal;
in vec2 screenCoord;
in float dist;
in vec3 that;
in vec3 lightDirection;
in vec2 UV;

out vec4 fragColor;
void main() {

    vec3 tex = texture(Sampler0, UV).rgb;
	vec2 uv = (screenCoord - 0.5) * 2.0;


	float d = dist-0.5;

    vec4 color = vec4(tex,d*0.5);
    fragColor = color*0.5 + vec4(0.4,0.3,0.2,0.0);
}























