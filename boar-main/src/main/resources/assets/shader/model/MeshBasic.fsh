#version 450 core

uniform sampler2D diffuseTex;

in vec2 uv;

out vec4 FragColor;

void main() {
    vec4 color = texture2D(diffuseTex, uv);
    if (color.a < 0.5) FragColor = vec4(1.0, 0.0, 0.0, 0.25);
    else FragColor = color;
}