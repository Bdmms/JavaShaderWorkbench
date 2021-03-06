#version 330 core
out vec4 FragColor;

in vec3 Position;
in vec3 Normal;
in vec2 Texture;

const vec3 lightPos = vec3( 0.0, 10.0, 0.0 );
const vec4 color = vec4( 0.7, 0.7, 0.9, 1.0 );

uniform sampler2D dif_texture;
uniform vec3 viewPos;

void main()
{
	vec3 lightDir = normalize(lightPos - Position);
	vec3 diffuse = color.rgb * max(dot(lightDir, Normal), 0.0);

	vec3 viewDir = normalize(viewPos - Position);
	vec3 halfwayDir = normalize(lightDir + viewDir);  
	float specular = pow(max(dot(Normal, halfwayDir), 0.0), 64.0);
	
	FragColor = vec4( (specular + diffuse) * 0.5 + color.rgb * 0.5, 1.0 );
}