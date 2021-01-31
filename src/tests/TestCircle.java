package tests;

import math.Circle;
import math.vec3f;

public class TestCircle 
{
	public void test_Circle() throws Exception
	{
		/*
		Circle circle = new Circle( new vec3f(), new vec3f( 1.0f, 0.0f, 0.0f ), new vec3f( 0.0f, 1.0f, 0.0f), 1.0f );
		float t0 = (float)(Math.random() * 2.0 * Math.PI);
		float t1 = (float)(Math.random() * 2.0 * Math.PI);
		float t2 = (float)(Math.random() * 2.0 * Math.PI);
		
		Circle result = new Circle( circle.getPoint( t0 ), circle.getPoint( t1 ), circle.getPoint( t2 ) );
		
		if( !circle.plane.v1.equals( result.plane.v1, 10.0f ) 
				|| Math.abs( result.radius - circle.radius ) >= 10.0f ) 
			throw new Exception( circle.plane.v1 + " vs. " + result.plane.v1 );*/
		
		for( int i = 0; i < 50; i++ )
		{
			vec3f p = vec3f.random( -100.0f, 100.0f );
			vec3f n = vec3f.random( -100.0f, 100.0f );
			vec3f u = new vec3f( n.y, n.x, n.z ).cross( n );
			vec3f v = n.cross( u );
			u.normalize();
			v.normalize();
			
			Circle circle = new Circle( p, u, v, (float)Math.random() * 10.0f + 1.0f );
			
			float t0 = (float)(Math.random() * 2.0 * Math.PI);
			float t1 = (float)(Math.random() * 2.0 * Math.PI);
			float t2 = (float)(Math.random() * 2.0 * Math.PI);
			
			if( t0 == t1 || t1 == t2 || t0 == t2 ) continue;
			
			Circle result = new Circle( circle.getPoint( t0 ), circle.getPoint( t1 ), circle.getPoint( t2 ) );
			
			if( !circle.plane.v1.equals( result.plane.v1, 1E-3f ) 
					|| Math.abs( result.radius - circle.radius ) >= 1E-3f ) 
				throw new Exception( circle.plane.v1 + " vs. " + result.plane.v1 );
		}
	}
}
