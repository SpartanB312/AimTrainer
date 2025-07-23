#version 450 core
#pragma optimize(on)

in vec4 color;
in vec2 uv;

out vec4 FragColor;

uniform sampler2D inputTexture;
uniform float sharpness;

#define Luminance(c) dot(c, vec3(0.2125, 0.7154, 0.0721))
#define min5(a, b, c, d, e) min(a, min(b, min(c, min(d, e))))
#define max5(a, b, c, d, e) max(a, max(b, max(c, max(d, e))))

vec3 CASPass(vec2 uv) {
    vec2 pixelSize = 1.0 / textureSize(inputTexture, 0);
    vec3 c00 = texture(inputTexture, uv + vec2(-pixelSize.x, -pixelSize.y)).rgb;
    vec3 c10 = texture(inputTexture, uv + vec2(0.0, -pixelSize.y)).rgb;
    vec3 c20 = texture(inputTexture, uv + vec2(pixelSize.x, -pixelSize.y)).rgb;
    vec3 c01 = texture(inputTexture, uv + vec2(-pixelSize.x, 0.0)).rgb;
    vec3 c11 = texture(inputTexture, uv).rgb;
    vec3 c21 = texture(inputTexture, uv + vec2(pixelSize.x, 0.0)).rgb;
    vec3 c02 = texture(inputTexture, uv + vec2(-pixelSize.x, pixelSize.y)).rgb;
    vec3 c12 = texture(inputTexture, uv + vec2(0.0, pixelSize.y)).rgb;
    vec3 c22 = texture(inputTexture, uv + vec2(pixelSize.x, pixelSize.y)).rgb;
    float b00 = Luminance(c00);
    float b10 = Luminance(c10);
    float b20 = Luminance(c20);
    float b01 = Luminance(c01);
    float b11 = Luminance(c11);
    float b21 = Luminance(c21);
    float b02 = Luminance(c02);
    float b12 = Luminance(c12);
    float b22 = Luminance(c22);
    float minBrightness = min(min(min(b00, b10), min(b20, b01)), min(min(b11, b21), min(b02, b12)));
    float maxBrightness = max(max(max(b00, b10), max(b20, b01)), max(max(b11, b21), max(b02, b12)));
    float contrast = maxBrightness - minBrightness;
    float sharpnessScale = 1.0 / (1.0 + contrast * 10.0);
    float sharpenFactor = clamp(sharpness * sharpnessScale, 0.0, 1.0);
    vec3 sharpenedColor = c11 * (1.0 + 8.0 * sharpness)
    - (c00 + c01 + c02 + c10 + c12 + c20 + c21 + c22) * sharpness;
    sharpenedColor = clamp(sharpenedColor, min(min(c10, c12), min(c01, c21)), max(max(c10, c12), max(c01, c21)));
    return mix(c11, sharpenedColor, sharpenFactor);
}

void main() {
    FragColor = vec4(CASPass(uv), 1.0);
}