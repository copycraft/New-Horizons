#version 330 core
out vec4 FragColor;

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;

// Signed Distance Function for a sphere at (0,0,0) with radius 1.0
float sphereSDF(vec3 p, float r) {
    return length(p) - r;
}

// Computes the direction of a ray from the camera through a pixel
vec3 getRayDirection(vec2 fragCoord, vec2 resolution, float fov) {
    // Convert pixel coordinate to screen space centered at (0,0)
    vec2 xy = fragCoord - resolution * 0.5;
    float z = resolution.y / tan(radians(fov) * 0.5);
    return normalize(vec3(xy, -z));
}

// Raymarching function: marches along the ray until it hits the sphere
vec3 raymarch(vec3 ro, vec3 rd) {
    float t = 0.0;
    const float tMax = 100.0;
    const int maxSteps = 100;
    for (int i = 0; i < maxSteps; i++) {
        vec3 pos = ro + rd * t;
        float d = sphereSDF(pos, 1.0);
        if (d < 0.001) {
            // Basic shading: approximate normal and use it for color
            vec3 normal = normalize(pos);
            return 0.5 + 0.5 * normal;
        }
        t += d;
        if (t > tMax) break;
    }
    // Background color
    return vec3(0.0);
}

void main() {
    vec2 fragCoord = gl_FragCoord.xy;
    
    // Set the camera position; placing it along the positive Z-axis looking towards the origin.
    vec3 cameraPos = vec3(0.0, 0.0, 5.0);
    
    // Compute the ray direction for the current fragment
    vec3 rd = getRayDirection(fragCoord, u_resolution, 45.0);
    
    // Obtain the color by raymarching from the camera along the ray
    vec3 color = raymarch(cameraPos, rd);
    
    FragColor = vec4(color, 1.0);
}

