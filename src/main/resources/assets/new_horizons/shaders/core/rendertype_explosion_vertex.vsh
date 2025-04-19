#version 150

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

in vec3 Position;
in vec3 Normal;
in vec2 UV0;
in vec4 Color;

out vec2 screenCoord;
out vec2 UV;
out float dist;
out vec3 that;
out vec3 normal;
out vec3 lightDirection;
out vec4 vcolor;

void main() {

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    screenCoord = gl_Position.xy;
    normal = Normal;
    lightDirection = normalize(Position);
    
    that = normal - normalize(Position);
    UV = UV0;
    float d = length(that);


    vcolor = Color;

    dist = 2 * cos( 3.1415 - (2.0 * acos(d/2)));

}






