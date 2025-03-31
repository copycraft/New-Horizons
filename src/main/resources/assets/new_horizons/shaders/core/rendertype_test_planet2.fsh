#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 cameraPos;

in vec4 texProj0;
in vec3 Cam;
out vec4 fragColor;

#define MAX_STEPS 100
#define MAX_DIST 50.0
#define SURF_DIST 0.01

// Signed Distance Function for a sphere
float sphereSDF(vec3 p, float radius) {
    return length(p) - radius;
}

// Normal calculation using central differences
vec3 getNormal(vec3 p) {
    float d = 0.001;
    return normalize(vec3(
    sphereSDF(p + vec3(d, 0, 0), 1.0) - sphereSDF(p - vec3(d, 0, 0), 1.0),
    sphereSDF(p + vec3(0, d, 0), 1.0) - sphereSDF(p - vec3(0, d, 0), 1.0),
    sphereSDF(p + vec3(0, 0, d), 1.0) - sphereSDF(p - vec3(0, 0, d), 1.0)
    ));
}

// Raymarching function
float rayMarch(vec3 ro, vec3 rd) {
    float depth = 0.0;
    for (int i = 0; i < MAX_STEPS; i++) {
        vec3 pos = ro + depth * rd;
        float dist = sphereSDF(pos, 1.0);
        if (dist < SURF_DIST) return depth;
        if (depth > MAX_DIST) break;
        depth += dist;
    }
    return -1.0;
}

void main() {
    vec2 coord = (texProj0.xy / texProj0.w) * 2.0 - 1.0;
    vec3 camPos = -Cam;
    vec3 rayDir = normalize(vec3(coord, 1.0));

    float dist = rayMarch(camPos, rayDir);

    if (dist > 0.0) {
        vec3 hitPos = camPos + rayDir * dist;
        vec3 normalAtHit = getNormal(hitPos);
        vec3 lightDir = normalize(vec3(1.0, 1.0, -1.0));

        float diff = max(dot(normalAtHit, lightDir), 0.0);
        vec3 viewDir = normalize(cameraPos - hitPos);
        vec3 reflectDir = reflect(-lightDir, normalAtHit);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);

        // Rim lighting to emphasize the edge
        float rim = pow(1.0 - dot(normalAtHit, lightDir), 3.0);
        vec3 finalColor = vec3(1.0, 0.0, 0.0) * diff + vec3(1.0) * spec + vec3(0.1, 0.1, 0.2) * rim;
        fragColor = vec4(finalColor, 1.0);
    } else {
        fragColor = textureProj(Sampler0, texProj0);
    }
}
