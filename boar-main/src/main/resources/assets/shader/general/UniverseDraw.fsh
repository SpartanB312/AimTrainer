#version 120

uniform sampler2D texture;
uniform int drawTex;

varying vec4 color;
varying vec2 uv;

void main() {
    if (drawTex == 1) gl_FragColor = color * texture2D(texture, uv);
    else gl_FragColor = color;
}