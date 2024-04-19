#version 140

uniform sampler2D diffuseTex;

in vec2 uv;

void main() {
    vec4 color = texture2D(diffuseTex, uv);
    if (color.a < 0.5) gl_FragColor = vec4(1.0, 0.0, 0.0, 0.25);
    else gl_FragColor = color;
}