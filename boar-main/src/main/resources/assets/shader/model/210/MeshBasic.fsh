#version 120

uniform sampler2D diffuseTex;

varying vec2 uv;

void main() {
    vec2 revUV = vec2(uv.x, 1.0-uv.y);
    vec4 color = texture2D(diffuseTex, revUV);
    if (color.a < 0.5) gl_FragColor = vec4(1.0, 0.0, 0.0, 0.25);
    else gl_FragColor = color;
}