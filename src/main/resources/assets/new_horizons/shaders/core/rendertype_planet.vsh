#version 150

in vec3 Position;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform sampler2D Sampler1;
uniform float HeightMapIntensity;

out vec2 texCoord0;
out vec3 normal;


void main() {

    float height = texture(Sampler1, UV0).r * HeightMapIntensity;

    vec3 displacedPosition = Position + (Normal * height);

    gl_Position = (ProjMat * ModelViewMat * vec4(displacedPosition, 1.0)) * 1.0;

//	float m = (gl_Position.z>200.0)?
//	(gl_Position.z - 200.0)*1.5 + 1.0:
//	1.0;
//
//	gl_Position.w *= m;
//	gl_Position.x *= m;
//	gl_Position.y *= m;
//	gl_Position.z /= m;

    normal = Normal;
    texCoord0 = UV0;
}


































































