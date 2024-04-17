#version 120

uniform sampler2D texture;

varying vec4 color;
varying vec2 uv;

void main() {
    gl_FragColor = color * texture2D(texture, uv);
}