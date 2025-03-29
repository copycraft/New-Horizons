#version 150

in vec3 Position;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform sampler2D Sampler1;

out vec2 texCoord0;

#define HEIGHTMAP_MULTIPLIER 0.15

void main() {

    float height = texture(Sampler1, UV0).r * HEIGHTMAP_MULTIPLIER;

    vec3 displacedPosition = Position + (Normal * height);

    gl_Position = ProjMat * ModelViewMat * vec4(displacedPosition, 1.0);

    texCoord0 = UV0;
}