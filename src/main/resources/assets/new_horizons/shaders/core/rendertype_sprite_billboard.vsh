#version 150

#moj_import <projection.glsl>

in vec3 Position;
in vec3 Normal;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 texProj0;
out vec2 texCoord0;

#define SIZE 120.0

void main() {
    vec2 Displacement = UV0.xy;
    Displacement -= 0.5;
    Displacement.y *= 2.;


    //Displacement -= 0.5;
    gl_Position = (ProjMat * ModelViewMat * vec4(Position, 1.0)) + (vec4(Displacement, 0., 0.) *  SIZE * 1.41421356237);
    texProj0 = vec4(Position, 1.0);
    texCoord0 = UV0;
}