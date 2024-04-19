#version 120

attribute in vec3 position;
attribute in vec2 texCoords;

varying vec2 uv;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * vec4(position, 1.0);
    uv = texCoords;
}