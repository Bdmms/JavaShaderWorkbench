#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texture;

out vec3 Position;
out vec3 Normal;
out vec2 Texture;

uniform float time;
uniform mat4 view;
uniform mat4 projection;

uniform float timeScale;
uniform float frequency;
uniform float amplitude;

const int size = 4;
const vec3 source[4] = { 
	vec3( 0.5, 0.0, 0.5),
	vec3(-0.5, 0.0, 0.5),
	vec3( 0.5, 0.0, -0.5),
	vec3(-0.5, 0.0, -0.5)
};

void main()
{
	vec3 prp = vec3(0.0, 0.0, 0.0);
	vec3 nrm = vec3(0.0, 0.0, 0.0);

	for( int i = 0; i < size; i++ )
	{
		vec3 prop = position - source[i];
		float dist = length(prop);
		float theta = time * timeScale - dist * frequency;
		float amp = amplitude * max(1.0 - dist, 0.0);
		prp += amp * sin(theta) * normal;
		nrm += normalize(normal - prop * amp * frequency * cos(theta));
	}

	vec4 pos = projection * view * vec4(position + prp, 1.0);

	Position = pos.xyz;
	Texture = texture;
	Normal = nrm / size;
	gl_Position = pos;
}