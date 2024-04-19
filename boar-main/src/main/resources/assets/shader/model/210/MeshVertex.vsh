#version 140

attribute vec3 position;
attribute vec3 normal;
attribute vec2 texCoords;

uniform mat4 matrix;

out vec2 uv;

void main() {
    gl_Position = matrix * vec4(position, 1.0);
    uv = texCoords;
}