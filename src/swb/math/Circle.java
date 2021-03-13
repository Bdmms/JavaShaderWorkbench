package swb.math;

import com.jogamp.opengl.math.FloatUtil;

public class Circle 
{
	public Triangle plane;
	public float radius;
	
	public Circle()
	{
		plane = new Triangle();
		radius = 0.0f;
	}
	
	public Circle( vec3f p, vec3f u, vec3f v, float r )
	{
		plane = new Triangle( p, u, v );
		radius = r;
	}
	
	public Circle( vec3f v1, vec3f v2, vec3f v3 )
	{
		vec3f e0 = v2.from( v1 );
		vec3f e1 = v3.from( v1 );
		vec3f n = e0.cross( e1 );
		
		vec3f m0 = new vec3f();
		vec3f m1 = new vec3f();
		vecf.add( m0, v1, v2 );
		vecf.add( m1, v1, v3 );
		m0.mul( 0.5f );
		m1.mul( 0.5f );
		
		vec3f n0 = n.cross( e0 );
		vec3f n1 = n.cross( e1 );
		n0.normalize();
		n1.normalize();
		
		vec3f p = Line.intersection( m1, m0, n1, n0 );
		vec3f d = v1.from( p );
		
		vec3f u = d.unit();
		vec3f v = n.cross( u ).unit();
		
		radius = d.length();
		plane = new Triangle( p, u, v );
	}
	
	public vec3f getPoint( float angle )
	{
		float sin = FloatUtil.sin( angle ) * radius;
		float cos = FloatUtil.cos( angle ) * radius;
		
		vec3f r = plane.v1.clone();
		r.addMul( plane.v2, cos );
		r.addMul( plane.v3, sin );
		return r;
	}
}
