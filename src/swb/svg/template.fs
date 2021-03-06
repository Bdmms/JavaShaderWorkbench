#version 330 core
out vec4 FragColor;

in vec3 Position;
in vec3 Normal;
in vec3 Tangent;
in vec3 BiTangent;
in vec2 Texture;

const vec3 lightPos = vec3( 0.0, 10.0, 0.0 );

uniform sampler2D dif_texture;
uniform sampler2D nrm_texture;

void main()
{
	vec3 nrmTxt = texture( nrm_texture, Texture ).rgb * 2.0 - 1.0;
	vec3 normal = normalize( nrmTxt.z * Normal + nrmTxt.x * Tangent + nrmTxt.y * BiTangent );
	
	vec4 color = texture( dif_texture, Texture );
	if( color.a < 0.5 )
		discard;

	vec3 ambience = 0.5 * color.rgb;
	vec3 lightDir = normalize(lightPos - Position);
	vec3 diffuse = color.rgb * max(dot(lightDir, Normal), 0.0) * 0.5;

	FragColor = vec4( diffuse + ambience, 1.0 );
}