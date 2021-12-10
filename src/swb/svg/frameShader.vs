#version 330 core
layout (location = 0) in vec2 position;

out vec2 Position;

const vec2 scaleVal = vec2( 0.5, -0.5 );

void main()
{
	Position = ( position * scaleVal ) + 0.5;
	gl_Position = vec4(position, 0.0, 1.0);
}