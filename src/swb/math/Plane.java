package swb.math;

public class Plane 
{
	public vec3f origin;
	public vec3f normal;
	public vec3f svec;
	public vec3f tvec;
	
	public Plane( vec3f v1, vec3f v2, vec3f v3 )
	{
		origin = v1;
		svec = vec3f.sub( v2, v1 );
		tvec = vec3f.sub( v3, v1 );
		normal = svec.cross( tvec );
	}
	
	public vec3f intersection( Line line )
	{
		vec3f lvec = vec3f.mul( line.vector, -1.0f );
		vec3f pt = vec3f.sub( line.origin, origin );
		float det = lvec.dot( normal );
		
		// Check if point is on plane
		if( det == 0.0f ) return pt.dot( normal ) == 0.0f ? vec3f.UNDEFINED : null;
		
		return new vec3f(
			normal.dot( pt ) / det,
			tvec.cross( lvec ).dot( pt ) / det,
			lvec.cross( svec ).dot( pt ) / det
		);
	}
	
	public vec3f getPoint( float s, float t )
	{
		return new vec3f(
				origin.x + svec.x * s + tvec.x * t,
				origin.y + svec.y * s + tvec.y * t,
				origin.z + svec.z * s + tvec.z * t
		);
	}
	
	public static void main( String[] args )
	{
		vec3f v1 = new vec3f( 1.0f, 1.0f, 0.0f );
		vec3f v2 = new vec3f( 1.0f, 0.0f, 0.0f );
		vec3f v3 = new vec3f( 0.0f, 1.0f, 0.0f );
		
		vec3f v4 = new vec3f( 0.0f, 0.0f, 1.0f );
		vec3f v5 = new vec3f( 0.0f, 0.0f, 0.0f );
		
		Plane plane = new Plane( v3, v2, v1 );
		Line line = new Line( v4, vec3f.sub( v5, v4 ) );
		
		vec3f result = plane.intersection( line );
		System.out.println( result == vec3f.UNDEFINED );
		System.out.println( result );
		System.out.println( line.getPoint( result.x ) );
		System.out.println( plane.getPoint( result.y, result.z ) );
	}
}
