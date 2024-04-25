#version 120

uniform sampler2D diffuseTex;
uniform sampler2D normalTex;
uniform sampler2D specularTex;
uniform sampler2D heightTex;

varying vec2 uv;

void main() {
    vec2 revUV = vec2(uv.x, 1.0-uv.y);
    vec4 color = texture2D(diffuseTex, revUV);
    vec4 color1 = texture2D(specularTex, revUV);
    float alpha = color.a;
    if (color1.a > color.a) alpha = color1.a;
    float red = color.r;
    float green = color.g;
    float blue = color.b;
    if (color.a != 1.0) {
        red += color1.r / 2.0;
        green +=  color1.g / 2.0;
        blue += color1.b / 2.0;
    }
    gl_FragColor = vec4(red, green, blue, 1.0);
}