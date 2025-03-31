#version 150

in vec3 Position;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform sampler2D Sampler1; // Heightmap

#define HEIGHTMAP_MULTIPLIER 0.4

out vec2 texCoord0;
out vec3 normal;

void main() {
    // Displace the vertex along its normal by the heightmap value
    float height = texture(Sampler1, UV0).r * HEIGHTMAP_MULTIPLIER;
    vec3 displacedPosition = Position + (Normal * height);

    gl_Position = ProjMat * ModelViewMat * vec4(displacedPosition, 1.0);
    normal = Normal;
    texCoord0 = UV0;
}
