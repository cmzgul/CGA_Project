#version 330 core

in vec3 TexCoords;

uniform samplerCube skyTexture;

out vec4 color;

void main()
{
    color = texture(skyTexture, TexCoords);
}