#version 450 core

uniform sampler2D diffuseTex;
uniform sampler2D normalTex;
uniform sampler2D specularTex;
uniform sampler2D heightTex;

in vec2 uv;

out vec4 FragColor;

void main() {
    vec4 color = texture2D(diffuseTex, uv);
    vec4 color1 = texture2D(specularTex, uv);
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
    //if (color.a == 0.0)  FragColor = vec4(1.0, 0.0, 0.0, 0.25);
    //else
    FragColor = vec4(red, green, blue, 1.0);
}