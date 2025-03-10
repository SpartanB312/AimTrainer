#version 450 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 vertColor;
layout (location = 2) in vec3 normal;

uniform mat4 matrix;

out vec4 color;
out vec3 fragPosVec;
out vec3 normaVec;

void main() {
    gl_Position = matrix * vec4(position, 1.0);
    color = vertColor.abgr;
    fragPosVec = position;
    normaVec = normal;
}