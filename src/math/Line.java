package math;

public class Line 
{
	public vec3f origin;
	public vec3f vector;
	
	public Line( vec3f v1, vec3f vec )
	{
		this.origin = v1;
		this.vector = vec;
	}
	
	public vec3f getPoint( float t )
	{
		return new vec3f( origin.x + vector.x * t, origin.y + vector.y * t, origin.z + vector.z * t );
	}
	
	public static vec3f intersection( vec3f v1, vec3f v2, vec3f l1, vec3f l2 )
	{
		// a + su = b + tv
		// s = (b + tv - a) / u
		
		// ay + (bx + t * vx - ax) * uy / ux = by + t * vy
		// ay + (bx - ax) * uy / ux - by = t * vy - t * vx * uy / ux
		// ay + (bx - ax) * uy / ux - by = t * (vy - vx * uy / ux)
		
		float dx = Math.abs( l1.x );
		float dy = Math.abs( l1.y );
		float dz = Math.abs( l1.z );
		
		if( dz < dx && dz < dy )
		{
			float rt = l1.y / l1.x;
			return vec3f.add( v2, vec3f.mul( l2, ( (v1.y - v2.y) + (v2.x - v1.x) * rt) / (l2.y - l2.x * rt ) ) );
		}
		else if( dy < dz && dy < dx )
		{
			float rt = l1.x / l1.z;
			return vec3f.add( v2, vec3f.mul( l2, ( (v1.x - v2.x) + (v2.z - v1.z) * rt) / (l2.x - l2.z * rt ) ) );
		}
		else if( dz != 0.0f )
		{
			float rt = l1.y / l1.z;
			return vec3f.add( v2, vec3f.mul( l2, ( (v1.y - v2.y) + (v2.z - v1.z) * rt) / (l2.y - l2.z * rt ) ) );
		}
		
		return null;
		
		/*
		vec3f d = vec3f.sub( l1, v1 );
		
		float s = d.cross( l2 ).dot( l1.cross( l2 ) ) / l1.cross( l2 ).length2();
		
		return vec3f.add( v1, vec3f.mul( l1, s ) );*/
		
		/*
		float den = l1.cross( l2 ).length();
		if( den == 0.0 ) return null;
		
		vec3f m = vec3f.mul( l2, vec3f.sub( v2, v1 ).cross( l2 ).length() / den );
		return vec3f.add( v1, m );*/
	}
}
