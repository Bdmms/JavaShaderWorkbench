#version 330 core
layout (location = 0) in vec2 position;

out vec2 Coord;

uniform vec2 location;
uniform vec2 scale;

void main()
{
	Coord = vec2( position.x, position.y );
	gl_Position = vec4( position * scale + location, 0.0, 1.0);
}