#version 330 core
layout (location = 0) in vec3 aPos;

out vec2 QuadPos;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

void main()
{
	QuadPos = aPos.xy;
	gl_Position = projection * view * model * vec4(aPos, 1.0);
}  