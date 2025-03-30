#version 150

in vec3 Position;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform sampler2D Sampler1;
uniform float GameTime;

out vec2 texCoord0;
out vec3 normal;

#define HEIGHTMAP_MULTIPLIER 0.05
void main() {

    float height = texture(Sampler1, UV0).r * HEIGHTMAP_MULTIPLIER +10.0;

    vec3 displacedPosition = Position + (Normal * height);

    gl_Position = ProjMat * ModelViewMat * vec4(displacedPosition, 1.0);
    normal = Normal;
    texCoord0 = UV0;
}







