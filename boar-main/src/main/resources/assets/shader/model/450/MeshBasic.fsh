#version 450 core

uniform sampler2D diffuseTex;

in vec2 uv;

out vec4 FragColor;

void main() {
    vec2 revUV = vec2(uv.x, 1.0-uv.y);
    vec4 color = texture2D(diffuseTex, revUV);
    if (color.a < 0.5) FragColor = vec4(1.0, 0.0, 0.0, 0.25);
    else FragColor = color;
}