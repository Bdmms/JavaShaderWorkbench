package swb.math;

public class mat4x4
{
	public vec4f r0;
	public vec4f r1;
	public vec4f r2;
	public vec4f r3;
	
	public mat4x4()
	{
		r0 = new vec4f( 1.0f, 0.0f, 0.0f, 0.0f );
		r1 = new vec4f( 0.0f, 1.0f, 0.0f, 0.0f );
		r2 = new vec4f( 0.0f, 0.0f, 1.0f, 0.0f );
		r3 = new vec4f( 0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	public mat4x4( vec4f r0, vec4f r1, vec4f r2, vec4f r3 )
	{
		this.r0 = r0;
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;
	}
	
	public mat4x4( vec3f translation, vec3f scale )
	{
		r0 = new vec4f( scale.x, 0.0f, 	  0.0f,    translation.x );
		r1 = new vec4f( 0.0f, 	 scale.y, 0.0f,    translation.y );
		r2 = new vec4f( 0.0f, 	 0.0f, 	  scale.z, translation.z );
		r3 = new vec4f( 0.0f, 	 0.0f, 	  0.0f,    1.0f );
	}
	
	public mat4x4( vec3f translation, vec3f rotation, vec3f scale )
	{
		float sinx = (float)Math.sin( rotation.x );
		float cosx = (float)Math.cos( rotation.x );
		float siny = (float)Math.sin( rotation.y );
		float cosy = (float)Math.cos( rotation.y );
		float sinz = (float)Math.sin( rotation.z );
		float cosz = (float)Math.cos( rotation.z );
		
		r0 = new vec4f( scale.x * cosy * cosz, -sinz, siny, translation.x );
		r1 = new vec4f( sinz, scale.y * cosx * cosz, -sinx, translation.y );
		r2 = new vec4f( -siny, sinx, scale.z * cosx * cosy, translation.z );
		r3 = new vec4f( 0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	public void mul( mat4x4 mat )
	{
		r0.mul( mat.r0 );
		r1.mul( mat.r1 );
		r2.mul( mat.r2 );
		r3.mul( mat.r3 );
	}
	
	public mat4x4 matMul( mat4x4 mat )
	{
		float a00 = r0.dot( mat.r0.x, mat.r1.x, mat.r2.x, mat.r3.x );
		float a01 = r0.dot( mat.r0.y, mat.r1.y, mat.r2.y, mat.r3.y );
		float a02 = r0.dot( mat.r0.z, mat.r1.z, mat.r2.z, mat.r3.z );
		float a03 = r0.dot( mat.r0.w, mat.r1.w, mat.r2.w, mat.r3.w );
		
		float a10 = r1.dot( mat.r0.x, mat.r1.x, mat.r2.x, mat.r3.x );
		float a11 = r1.dot( mat.r0.y, mat.r1.y, mat.r2.y, mat.r3.y );
		float a12 = r1.dot( mat.r0.z, mat.r1.z, mat.r2.z, mat.r3.z );
		float a13 = r1.dot( mat.r0.w, mat.r1.w, mat.r2.w, mat.r3.w );
		
		float a20 = r2.dot( mat.r0.x, mat.r1.x, mat.r2.x, mat.r3.x );
		float a21 = r2.dot( mat.r0.y, mat.r1.y, mat.r2.y, mat.r3.y );
		float a22 = r2.dot( mat.r0.z, mat.r1.z, mat.r2.z, mat.r3.z );
		float a23 = r2.dot( mat.r0.w, mat.r1.w, mat.r2.w, mat.r3.w );
		
		float a30 = r3.dot( mat.r0.x, mat.r1.x, mat.r2.x, mat.r3.x );
		float a31 = r3.dot( mat.r0.y, mat.r1.y, mat.r2.y, mat.r3.y );
		float a32 = r3.dot( mat.r0.z, mat.r1.z, mat.r2.z, mat.r3.z );
		float a33 = r3.dot( mat.r0.w, mat.r1.w, mat.r2.w, mat.r3.w );
		
		return new mat4x4(
				new vec4f( a00, a01, a02, a03 ),
				new vec4f( a10, a11, a12, a13 ),
				new vec4f( a20, a21, a22, a23 ),
				new vec4f( a30, a31, a32, a33 ) );
	}
	
	public void transform( float[] vec, int offset )
	{
		int idx = offset;
		float x = vec[idx++];
		float y = vec[idx++];
		float z = vec[idx  ];
		vec[offset++] = r0.dot( x, y, z, 1.0f );
		vec[offset++] = r1.dot( x, y, z, 1.0f );
		vec[offset  ] = r2.dot( x, y, z, 1.0f );
	}
	
	public vec3f transform( vec3f vector )
	{
		return new vec3f( 
				r0.dot( vector, 1.0f ), 
				r1.dot( vector, 1.0f ), 
				r2.dot( vector, 1.0f ) );
	}
	
	public vec4f transform( vec4f vector )
	{
		return new vec4f( 
				vector.dot( r0 ), 
				vector.dot( r1 ), 
				vector.dot( r2 ), 
				vector.dot( r3 ) );
	}
	
	public vec4f get( int i )
	{
		switch( i )
		{
		case 0: return r0;
		case 1: return r1;
		case 2: return r2;
		case 3: return r3;
		default: return null;
		}
	}
	
	public static mat4x4 rotatation( float rx, float ry, float rz )
	{
		float sinx = (float)Math.sin( rx );
		float cosx = (float)Math.cos( rx );
		float siny = (float)Math.sin( ry );
		float cosy = (float)Math.cos( ry );
		float sinz = (float)Math.sin( rz );
		float cosz = (float)Math.cos( rz );
		
		return new mat4x4( 	new vec4f( cosy * cosz, -sinz, siny, 0.0f ),
							new vec4f( sinz, cosx * cosz, -sinx, 0.0f ),
							new vec4f( -siny, sinx, cosx * cosy, 0.0f ),
							new vec4f( 0.0f, 0.0f, 0.0f, 1.0f ) );
	}
}
