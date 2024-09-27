#version 450 core

uniform float time;
uniform vec2 resolution;

out vec4 FragColor;

#define HEIGHT 12.
#define PI 3.14159
#define TAU 6.28318
#define TMAX 6.0

vec3 spunk(vec2 uv){
    vec3 col = vec3(.55, 0.35, 1.225);
    uv.x += sin(0.2+uv.y*0.8)*0.5;
    uv.x = uv.x*50.0;
    float dx = fract(uv.x);
    uv.x = floor(uv.x);
    float t =  time*0.4;
    uv.y *= 0.15;
    float o=sin(uv.x*215.4);
    float s=cos(uv.x*33.1)*.3 +.7;
    float trail = mix(95.0, 35.0, s);
    float yv = fract(uv.y + t*s + o) * trail;
    yv = 1.0/yv;
    yv = smoothstep(0.0, 1.0, yv*yv);
    yv = sin(yv*PI)*(s*5.0);
    float d2 = sin(dx*PI);
    yv *= d2*d2;
    col = col*yv;
    return col;
}

void mainImage(out vec4 fragColor, in vec2 fragCoord){
    float an = sin(time*0.8);
    float dist = 28.0;
    vec3 ro = vec3(dist*cos(an), sin(time*0.75)*14.0, dist*sin(an));
    vec3 ta = vec3(0.0, 0.0, 0.0);
    vec3 ww = normalize(ta - ro);
    vec3 uu = normalize(cross(ww, vec3(0.0, 1.0, 0.0)));
    vec3 vv = normalize(cross(uu, ww));
    vec3 tot = vec3(0.0);
    vec2 ppp = (-resolution.xy + 2.*(fragCoord))/resolution.y;
    vec3 bbk = spunk(ppp.xy);
    vec2 p = (-resolution.xy + 2.0*fragCoord)/resolution.y;
    vec3 rd = normalize(p.x*uu + p.y*vv + 1.5*ww);
    float t = 0.0;
    for (int i=0; i<10; i++){
        vec3 pos = ro + t*rd;
    }
    float v = 1.0-abs(p.y);
    vec3 col = bbk*v*2.0;
    if (t<TMAX){
        vec3 pos = ro + t*rd;
        vec3 dir = normalize(vec3(1.0, 0.7, 0.0));
    }
    col = sqrt(col);
    tot += col;
    fragColor = vec4(tot, 1.0);
}

void main(void){
    mainImage(FragColor, gl_FragCoord.xy);
}