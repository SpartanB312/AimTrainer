#version 450 core

uniform sampler2D texture;

in vec4 color;
in vec2 uv;

out vec4 FragColor;

void main() {
    if (uv.x >= -100000.0) FragColor = color * texture2D(texture, uv);
    else FragColor = color;
}