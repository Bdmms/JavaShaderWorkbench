#version 330 core
out vec4 FragColor;

in vec2 Texture;

uniform sampler2D diffuse;

void main()
{
	vec4 color = texture( diffuse, Texture );

	if( color.a < 0.5 )
		discard;

	FragColor = vec4( color.rgb, 1.0 );
}