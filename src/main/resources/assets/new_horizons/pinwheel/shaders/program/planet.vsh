#version 450 core
layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inNormal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

out vec3 fragNormal;

void main() {
    vec3 spherePos = normalize(inPosition);
    float radius = 1.0;
    spherePos *= radius;

    gl_Position = projectionMatrix * modelViewMatrix * vec4(spherePos, 1.0);
    fragNormal = normalize(inNormal);
}
