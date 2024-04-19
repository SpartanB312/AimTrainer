#version 450 core

uniform sampler2D texture;
uniform vec4 scissor;
uniform int drawTex;

in vec4 color;
in vec2 uv;

out vec4 FragColor;

void main() {
    if (drawTex == 1) FragColor = color * texture2D(texture, uv);
    else FragColor = color;
}