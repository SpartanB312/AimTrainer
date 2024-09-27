#version 450 core

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

out vec4 FragColor;

vec2 rotate(vec2 p, float a){
    return vec2(p.x * cos(a) - p.y * sin(a), p.x * sin(a) + p.y * cos(a));
}

float rand(float n){
    return fract(sin(n) * 43758.5453123);
}

vec2 rand2(in vec2 p){
    return fract(vec2(sin(p.x * 591.32 + p.y * 154.077), cos(p.x * 391.32 + p.y * 49.077)));
}

float noise1(float p){
    float fl = floor(p);
    float fc = fract(p);
    return mix(rand(fl), rand(fl + 1.0), fc);
}

float voronoi(in vec2 x){
    vec2 p = floor(x);
    vec2 f = fract(x);
    vec2 res = vec2(8.0);
    for (int j = -1; j <= 1; j ++){
        for (int i = -1; i <= 1; i ++){
            vec2 b = vec2(i, j);
            vec2 r = vec2(b) - f + rand2(p + b);
            float d = max(abs(r.x), abs(r.y));
            if (d < res.x){
                res.y = res.x;
                res.x = d;
            }
            else if (d < res.y){
                res.y = d;
            }
        }
    }
    return res.y - res.x;
}

void main(void){
    float flicker = noise1(time * 1.5) * 0.8 + 0.4;
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv = (uv - 0.5) * 2.0;
    vec2 suv = uv;
    uv.x *= resolution.x / resolution.y;
    float v = 0.0;
    uv = rotate(uv, sin(0.0 * 0.3) * 1.0);
    float a = 0.6, f = 1.0;
    for (int i = 0; i < 3; i ++){
        float v1 = voronoi(uv * f + 5.0);
        float v2 = 0.0;

        if (i > 0){
            v2 = voronoi(uv * f * 0.5 + 50.0 + time * 0.5);
            float va = 0.0, vb = 0.0;
            va = 1.0 - smoothstep(0.0, 0.1, v1);
            vb = 1.0 - smoothstep(0.0, 0.08, v2);
            v += a * pow(va * (0.5 + vb), 2.0);
        }

        v1 = 1.0 - smoothstep(0.0, 0.3, v1);
        v2 = a * (noise1(v1 * 5.5 + 0.1));
        if (i == 0) v += v2 * flicker;
        else v += v2;

        f *= 3.0;
        a *= 0.7;
    }

    v *= exp(-0.5 * length(suv)) * 1.0;
    vec3 cexp = vec3(2.0, 2.0, 1.0);
    cexp *= 1.3;
    vec3 col = vec3(pow(v, cexp.x), pow(v, cexp.y), pow(v, cexp.z)) * 2.0;

    FragColor = vec4(col, 1.0);
}