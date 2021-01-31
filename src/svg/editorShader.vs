#version 330 core
layout (location = 0) in vec2 position;
layout (location = 1) in vec4 color;

out vec4 Color;

uniform vec2 location;
uniform vec2 scale;

void main()
{
	Color = color;
	gl_Position = vec4(position * scale + location, 0.0, 1.0);
}