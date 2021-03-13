package swb.math;

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
		vec3f result = origin.clone();
		result.addMul( vector, t );
		return result;
	}
	
	public static vec3f intersection( vec3f v1, vec3f v2, vec3f l1, vec3f l2 )
	{
		// a + su = b + tv
		// s = (b + tv - a) / u
		
		// ay + (bx + t * vx - ax) * uy / ux = by + t * vy
		// ay + (bx - ax) * uy / ux - by = t * vy - t * vx * uy / ux
		// ay + (bx - ax) * uy / ux - by = t * (vy - vx * uy / ux)
		
		float dx = Math.abs( l1.getX() );
		float dy = Math.abs( l1.getY() );
		float dz = Math.abs( l1.getZ() );
		
		if( dz < dx && dz < dy )
		{
			float rt = l1.getY() / l1.getX();
			vec3f result = v2.clone();
			result.addMul( l2, ( (v1.getY() - v2.getY()) + (v2.getX() - v1.getX()) * rt) / (l2.getY() - l2.getX() * rt ) );
			return result;
		}
		else if( dy < dz && dy < dx )
		{
			float rt = l1.getX() / l1.getZ();
			vec3f result = v2.clone();
			result.addMul( l2, ( (v1.getX() - v2.getX()) + (v2.getZ() - v1.getZ()) * rt) / (l2.getX() - l2.getZ() * rt ) );
			return result;
		}
		else if( dz != 0.0f )
		{
			float rt = l1.getY() / l1.getZ();
			vec3f result = v2.clone();
			result.addMul( l2, ( (v1.getY() - v2.getY()) + (v2.getZ() - v1.getZ()) * rt) / (l2.getY() - l2.getZ() * rt ) );
			return result;
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
