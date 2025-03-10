#version 450 core

uniform vec3 viewPos;
uniform vec3 lightPos;
uniform vec3 lightColor;

in vec4 color;
in vec3 fragPosVec;
in vec3 normaVec;

out vec4 FragColor;

void main() {
    vec3 origin = vec3(color.rgb);
    // Ambient
    float ambientStrength = 0.3f;
    vec3 ambientColor = ambientStrength * lightColor;
    // Diffuse
    vec3 norm = normalize(normaVec);
    vec3 lightDir = normalize(lightPos - fragPosVec);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffColor = diff * lightColor;
    // Specular
    float specularStrength = 0.5f;
    vec3 viewDir = normalize(viewPos - fragPosVec);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specularColor = specularStrength * spec * lightColor;
    // Result
    vec3 result = (ambientColor + diffColor) * origin;
    FragColor = vec4(result, 1.0);
}