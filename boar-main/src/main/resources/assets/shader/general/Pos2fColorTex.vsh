#version 120

attribute vec2 position;
attribute vec4 vertColor;
attribute vec2 texCoords;

varying vec4 color;
varying vec2 uv;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * vec4(position, 0.0, 1.0);
    color = vertColor.abgr;
    uv = texCoords;
}