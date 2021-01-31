#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texture;

out vec3 Position;
out vec2 Texture;

uniform float time;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	vec4 pos = vec4(position, 1.0) * view * projection;
	Position = pos.xyz;
	Texture = texture;
	gl_Position = pos;
}