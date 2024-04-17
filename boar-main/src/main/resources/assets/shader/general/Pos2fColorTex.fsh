#version 120

uniform sampler2D texture;

varying vec4 color;
varying vec2 uv;

void main() {
    vec4 texColor = texture2D(texture, uv);
    float alpha = texColor.a;
    if (alpha < 0.01) discard;
    gl_FragColor = color * texColor;
}