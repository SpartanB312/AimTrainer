#version 120

attribute vec3 position;
attribute vec2 texCoords;
attribute vec3 normal;

uniform mat4 matrix;

varying vec2 uv;

void main() {
    gl_Position = matrix * vec4(position, 1.0);
    uv = texCoords;
}