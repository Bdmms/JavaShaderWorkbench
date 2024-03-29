package tests;

import swb.math.Line;
import swb.math.vec3f;

public class TestLine 
{
	public void test_Intersection() throws Exception
	{
		for( int i = 0; i < 50; i++ )
		{
			vec3f p = vec3f.random( -100.0f, 100.0f );
			vec3f l0 = vec3f.random( -100.0f, 100.0f );
			vec3f l1 = vec3f.random( -100.0f, 100.0f );
			
			if( l0.equals( l1, 1E-8f ) ) continue;
			
			vec3f v0 = p.clone();
			v0.addMul( l0, (float) Math.random() * 2.0f - 1.0f );
			
			vec3f v1 = p.clone();
			v1.addMul( l1, (float) Math.random() * 2.0f - 1.0f );
			
			if( v0.equals( vec3f.ZERO, 1E-8f ) || v1.equals( vec3f.ZERO, 1E-8f ) ) continue;
			
			vec3f o = Line.intersection( v0, v1, l0, l1 );
			
			if( o == null || !o.equals( p, 1E-3f ) ) throw new Exception( p + " vs. " + o );
		}
	}
}
