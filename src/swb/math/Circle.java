package swb.math;

public class Circle 
{
	public mat3x3 plane;
	public float radius;
	
	public Circle()
	{
		plane = new mat3x3();
		radius = 0.0f;
	}
	
	public Circle( vec3f p, vec3f u, vec3f v, float r )
	{
		plane = new mat3x3( p, u, v );
		radius = r;
	}
	
	public Circle( vec3f v1, vec3f v2, vec3f v3 )
	{
		vec3f e0 = vec3f.sub( v2, v1 );
		vec3f e1 = vec3f.sub( v3, v1 );
		vec3f n = e0.cross( e1 );
		
		vec3f m0 = vec3f.add( v1, v2 );
		vec3f m1 = vec3f.add( v1, v3 );
		m0.mul( 0.5f );
		m1.mul( 0.5f );
		
		vec3f n0 = n.cross( e0 );
		vec3f n1 = n.cross( e1 );
		n0.normalize();
		n1.normalize();
		
		vec3f p = Line.intersection( m1, m0, n1, n0 );
		vec3f d = vec3f.sub( v1, p );
		vec3f u = d.normalized();
		vec3f v = n.cross( u ).normalized();
		
		radius = d.length();
		plane = new mat3x3( p, u, v );
	}
	
	public vec3f getPoint( float angle )
	{
		float sin = (float)Math.sin( angle ) * radius;
		float cos = (float)Math.cos( angle ) * radius;
		
		return new vec3f( 
				plane.v1.x + cos * plane.v2.x + sin * plane.v3.x,
				plane.v1.y + cos * plane.v2.y + sin * plane.v3.y,
				plane.v1.z + cos * plane.v2.z + sin * plane.v3.z
		);
	}
}
