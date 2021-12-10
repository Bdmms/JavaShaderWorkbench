#version 330 core
out vec4 FragColor;

in vec2 Position;

vec4 circleMask( vec4 inside, vec4 outside, vec2 loc, vec2 scale )
{
	if( length( (Position - loc) / ( scale * 0.5 ) ) < 1.0 ) return inside;
	return outside;
}

vec4 circleLight( vec4 inside, vec4 outside, vec2 loc, vec2 scale, vec2 direction, vec2 point, float threshold )
{
	vec2 v0 = ( Position - loc) / ( scale * 0.5 );
	vec2 v1 = vec2( dot( normalize( direction ), normalize( v0 ) ), length( v0 ) );
	
	if( length( v1 - point ) > threshold ) return outside;
	return inside;
}

vec4 circleGrad()
{
	return vec4( 0.0, 0.0, 1.0, 1.0 );
}

void main()
{
	vec4 col0 = vec4( 1.0, 1.0, 1.0, 1.0 );
	col0 = circleMask( vec4( 0.258, 0.381, 0.527, 1.0 ), col0, vec2( 0.500, 0.500 ), vec2( 0.325, 0.400 ) );
	col0 = circleMask( vec4( 0.15, 0.15, 0.15, 1.0 ), col0, vec2( 0.500, 0.500 ), vec2( 0.225, 0.275 ) );
	col0 = circleMask( vec4( 0.258, 0.381, 0.527, 1.0 ), col0, vec2( 0.500, 0.500 ), vec2( 0.200, 0.250 ) );

	vec4 col1 = vec4( 1.0, 1.0, 1.0, 1.0 );
	col1 = circleMask( vec4( 0,0,0, 1.0 ), col1, vec2( 0.500, 0.500 ), vec2( 0.375, 0.450 ) );
	col0 = circleMask( col0, col1, vec2( 0.500, 0.500 ), vec2( 0.325, 0.400 ) );
	
	col0 = circleMask( vec4( 0.258, 0.381, 0.527, 1.0 ), col0, vec2( 0.500, 0.500 ), vec2( 0.200, 0.250 ) );

	col0 = circleMask( vec4( 1.000, 1.000, 1.000, 1.0 ), col0, vec2( 0.36, 0.55 ), vec2( 0.08, 0.04 ) );
	col0 = circleMask( vec4( 0.000, 0.000, 0.000, 1.0 ), col0, vec2( 0.5, 0.5 ), vec2( 0.05, 0.10 ) );
	col0 = circleMask( vec4( 1.000, 1.000, 1.000, 1.0 ), col0, vec2( 0.51, 0.52 ), vec2( 0.015, 0.03 ) );

	FragColor = col0;
}