#version 330 core

//input from vertex shader

in vec3 Normal;

struct Material{
    sampler2D diff;
    sampler2D emit;
    sampler2D specular;
    float shininess;
};


uniform Material material;
uniform vec3 PointLightColor, PointLightAttenuationFactors;
uniform vec3 SpotLightFrontColor, SpotLightFrontDirection, SpotLightFrontAttenuationFactors;
uniform float SpotLightFrontOuterAngle, SpotLightFrontInnerAngle;

//fragment shader output
out vec4 color;



void main(){
   vec3 normals= normalize(Normal); // damit vektoren gleiche länge haben
    color= vec4 (normals.rgb,1.0f);
}
