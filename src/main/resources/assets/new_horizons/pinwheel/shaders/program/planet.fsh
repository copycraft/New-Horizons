#version 150

uniform sampler2D CustomTexture;
uniform vec3 sphereCenter;
uniform float sphereRadius;
uniform vec3 lightPosition;
uniform vec3 cameraPosition;

in vec2 texCoord;
out vec4 fragColor;

// Function to calculate ray-sphere intersection
bool intersectSphere(vec3 rayOrigin, vec3 rayDir, out vec3 hitPoint, out vec3 normal) {
    vec3 oc = rayOrigin - sphereCenter;
    float a = dot(rayDir, rayDir);
    float b = 2.0 * dot(oc, rayDir);
    float c = dot(oc, oc) - sphereRadius * sphereRadius;
    float discriminant = b * b - 4.0 * a * c;
    if (discriminant < 0.0) {
        return false;
    } else {
        float t = (-b - sqrt(discriminant)) / (2.0 * a);
        hitPoint = rayOrigin + t * rayDir;
        normal = normalize(hitPoint - sphereCenter);
        return true;
    }
}

void main() {
    vec3 rayOrigin = cameraPosition;
    vec3 rayDir = normalize(vec3(texCoord, -1.0) - rayOrigin);
    vec3 hitPoint, normal;

    if (intersectSphere(rayOrigin, rayDir, hitPoint, normal)) {
        vec3 lightDir = normalize(lightPosition - hitPoint);
        float diff = max(dot(normal, lightDir), 0.0);
        vec3 diffuse = diff * texture(CustomTexture, texCoord).rgb;
        fragColor = vec4(diffuse, 1.0);
    } else {
        discard;
    }
}
