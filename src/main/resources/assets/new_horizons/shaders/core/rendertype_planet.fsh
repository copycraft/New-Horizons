#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec3 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }

    vec3 light = vec3(0.,0.0,1.);

    light = normalize(light);

    float lightInstensity = 3.;

    vec3 normalMap = vec3(texture(Sampler2, texCoord0).rg,0.5) * 2.0 - 1.0;

    normalMap *= 2.;

    vec3 tNormal = normalize(normal + normalMap);

    float shadow = max(dot(tNormal, light), 0.05) * lightInstensity;


    color.rgb *= shadow;

    //color.rgb = normalMap;

    fragColor = color * ColorModulator;
}