#version 150

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

in vec3 Position;
in vec3 Normal;
in vec2 UV0;

out vec2 screenCoord;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    screenCoord = gl_Position.xy;
}
