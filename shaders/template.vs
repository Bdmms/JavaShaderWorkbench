#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texture;

out vec2 Texture;

uniform float time;
uniform mat4 view;

void main()
{
	gl_Position = vec4(position, 1.0) * view;
	Texture = texture;
}