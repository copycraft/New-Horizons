#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform vec4 ColorModulator;

// Lighting uniforms
uniform vec3 lightDir;         // Should be normalized
uniform vec3 lightColor;
uniform float ambientStrength;
uniform float specularStrength;
uniform float shininess;

in vec2 texCoord0;
in vec3 normal;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    if (baseColor.a == 0.0) {
        discard;
    }

    // Sample the normal map and combine with the vertex normal
    vec3 normalMap = vec3(texture(Sampler2, texCoord0).rg, 0.5) * 2.0 - 1.0;
    vec3 finalNormal = normalize(normal + normalMap);

    // Compute ambient, diffuse, and specular lighting components
    vec3 ambient = ambientStrength * lightColor;
    float diff = max(dot(finalNormal, normalize(lightDir)), 0.0);
    vec3 diffuse = diff * lightColor;
    vec3 viewDir = normalize(vec3(0.0, 0.0, 1.0));
    vec3 halfDir = normalize(normalize(lightDir) + viewDir);
    float spec = pow(max(dot(finalNormal, halfDir), 0.0), shininess);
    vec3 specular = specularStrength * spec * lightColor;

    vec3 lighting = ambient + diffuse + specular;
    vec3 finalColor = baseColor.rgb * lighting;

    // Gamma correction (adjust gamma value as needed)
    finalColor = pow(finalColor, vec3(1.0/2.2));

    fragColor = vec4(finalColor, baseColor.a) * ColorModulator;
}
