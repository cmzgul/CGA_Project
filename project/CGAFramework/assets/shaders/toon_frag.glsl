#version 330 core

//input from vertex shader

in vec2 tc0;
in vec3 Normal, toLight, toCamera, spotLight;

struct Material{
    sampler2D diff;
    sampler2D emit;
    sampler2D specular;
    float shininess;
};

const float levels = 3.0f;

uniform Material material;
uniform vec3 PointLightColor, PointLightAttenuationFactors;
uniform vec3 SpotLightColor, SpotLightDirection, SpotLightAttenuationFactors;
uniform float SpotLightOuterAngle, SpotLightInnerAngle;


//fragment shader output
out vec4 color;

vec3 BRDF(vec3 N, vec3 L, vec3 V, vec3 R, float shininess){
    vec3 halfDir = normalize(L + V);
    float brightness = max(dot(N, L), 0.0f);
    float level = floor(brightness * levels);
    brightness = level / levels;
    float dampedFactor = pow(max(dot(N, halfDir), 0.0f), shininess);
    level = floor(dampedFactor * levels);
    dampedFactor = level / levels;
    return texture(material.diff, tc0).xyz * brightness + texture(material.specular, tc0).xyz * dampedFactor;
}

float calculateAttenuation(vec3 attenuationFactors, vec3 toLight){
    float distance = length(toLight);
    return 1.0f / (attenuationFactors.x + attenuationFactors.y * distance + attenuationFactors.z * (distance * distance));
}

vec3 calcSpotLight(vec3 normal, vec3 sLight, vec3 vDir, vec3 direction, float innerAngle, float outerAngle, vec3 color, float shininess, vec3 attenuationFactor){
    vec3 N = normalize(normal);
    vec3 L = normalize(sLight);
    vec3 V = normalize(vDir);
    vec3 R = reflect(-L, N);

    //Spotlight
    float theta = dot(L, normalize(-direction));
    float epsilon = (innerAngle - outerAngle);
    float intensity = clamp((theta - outerAngle) / epsilon, 0.0f, 1.0f);

    return BRDF(N,L,V,R, shininess) * intensity * color * calculateAttenuation(attenuationFactor, sLight);
}

vec3 calculatePointLights(vec3 normal, vec3 pLight, vec3 vDir, vec3 color, vec3 attenuationFactor, float shininess){
    vec3 N = normalize(normal);
    vec3 L = normalize(pLight);
    vec3 V = normalize(vDir);
    vec3 R = normalize(reflect(-L, N));

    vec3 EmmisiveTerm = texture(material.emit, tc0).xyz;

    return (BRDF(N,L,V,R, shininess) * color * calculateAttenuation(attenuationFactor, pLight));
}

void main(){
    color = vec4(texture(material.emit, tc0).xyz, 1.0f);
    color += vec4(texture(material.diff, tc0).xyz, 1.0f);
    color += vec4(calculatePointLights(Normal, toLight, toCamera, PointLightColor, PointLightAttenuationFactors, material.shininess), 1.0f);
    color += vec4(calcSpotLight(Normal, spotLight, toCamera, SpotLightDirection, SpotLightInnerAngle, SpotLightOuterAngle, SpotLightColor, material.shininess, SpotLightAttenuationFactors), 1.0f);
}
