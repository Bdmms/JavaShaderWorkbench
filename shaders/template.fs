#version 330 core
out vec4 FragColor;

in vec3 Position;
in vec2 Texture;

const vec3 light = vec3( 0.0, 0.0, -0.25 );

uniform sampler2D diffuse;

void main()
{
	vec4 color = texture( diffuse, Texture );

	if( color.a < 0.5 )
		discard;

	float intensity = 1.0 - length( light - Position );
	FragColor = vec4( color.rgb * intensity, 1.0 );
}