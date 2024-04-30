#version 450 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) in vec3 normal;

uniform mat4 matrix;

out vec2 uv;

void main() {
    gl_Position = matrix * vec4(position, 1.0);
    uv = texCoords;
}