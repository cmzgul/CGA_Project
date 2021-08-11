#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 projection;
uniform mat4 view;

out vec3 TexCoords;

void main()
{
    TexCoords = aPos.xyz;
    vec4 pos = projection * view * vec4(aPos.xyz , 1.0f);
    gl_Position = pos.xyww;
}