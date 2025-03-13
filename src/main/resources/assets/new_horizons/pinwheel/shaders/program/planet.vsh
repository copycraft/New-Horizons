#version 330 core
layout (location = 0) in vec3 aPos;

void main()
{
    // Pass the position (as a homogeneous coordinate) to the geometry shader.
    gl_Position = vec4(aPos, 1.0);
}
