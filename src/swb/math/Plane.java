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
		svec = v2.from( v1 );
		tvec = v3.from( v1 );
		normal = svec.cross( tvec );
	}
	
	public vec3f intersection( Line line )
	{
		vec3f lvec = line.vector.clone();
		lvec.mul( -1.0f );
		
		vec3f pt = line.origin.from( origin );
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
		vec3f r = origin.clone();
		r.addMul( svec, s );
		r.addMul( tvec, t );
		return r;
	}
	
	public static void main( String[] args )
	{
		vec3f v1 = new vec3f( 1.0f, 1.0f, 0.0f );
		vec3f v2 = new vec3f( 1.0f, 0.0f, 0.0f );
		vec3f v3 = new vec3f( 0.0f, 1.0f, 0.0f );
		
		vec3f v4 = new vec3f( 0.0f, 0.0f, 1.0f );
		vec3f v5 = new vec3f( 0.0f, 0.0f, 0.0f );
		
		Plane plane = new Plane( v3, v2, v1 );
		Line line = new Line( v4, v5.from( v4 ) );
		
		vec3f result = plane.intersection( line );
		System.out.println( result == vec3f.UNDEFINED );
		System.out.println( result );
		System.out.println( line.getPoint( result.getX() ) );
		System.out.println( plane.getPoint( result.getY(), result.getZ() ) );
	}
}
