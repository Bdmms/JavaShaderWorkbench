#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texture;

out vec3 Position;
out vec3 Normal;
out vec3 Tangent;
out vec3 BiTangent;
out vec2 Texture;

uniform float time;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	vec4 pos = projection * view * vec4(position, 1.0);
	Position = pos.xyz;
	Texture = texture;
	Normal = normal;
	Tangent = normal;
	BiTangent = cross(normal, normal);
	gl_Position = pos;
}