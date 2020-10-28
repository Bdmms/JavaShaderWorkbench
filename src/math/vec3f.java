package math;

import java.util.List;

public class vec3f extends vec2f
{
	public static final vec3f UNDEFINED = new vec3f( 0.0f, 0.0f, 0.0f ); 
	public static final vec3f ZERO = new vec3f( 0.0f, 0.0f, 0.0f ); 
	
	public float z;
	
	public vec3f()
	{
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}
	
	public vec3f( float scalar )
	{
		this.x = scalar;
		this.y = scalar;
		this.z = scalar;
	}
	
	public vec3f( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public vec3f( String[] elements )
	{
		x = Float.parseFloat( elements[1] );
		y = Float.parseFloat( elements[2] );
		
		if( elements.length > 3 )
			z = Float.parseFloat( elements[3] );
		else
			z = 0.0f;
	}
	
	public String toString()
	{
		return x + ", " + y + ", " + z;
	}
	
	public boolean equals( vec3f vector )
	{
		return vector.x == x && vector.y == y && vector.z == z;
	}
	
	public void add( vec3f vector )
	{
		x += vector.x;
		y += vector.y;
		z += vector.z;
	}
	
	public void mul( float scalar )
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	
	public float dot( vec3f vec )
	{
		return x * vec.x + y * vec.y + z * vec.z;
	}
	
	public vec3f projectOn( vec3f vector )
	{
		return vec3f.mul( vector, dot( vector ) / vector.dot( vector ) );
	}
	
	public float length()
	{
		return (float)Math.sqrt( x * x + y * y + z * z );
	}
	
	public vec3f unit()
	{
		return mul( this, 1.0f / length() );
	}
	
	public vec3f cross( vec3f vec )
	{
		return new vec3f( 
				y * vec.z - z * vec.y, 
				z * vec.x - x * vec.z, 
				x * vec.y - y * vec.x
		);
	}
	
	public static vec3f sub( vec3f a, vec3f b )
	{
		return new vec3f( a.x - b.x, a.y - b.y, a.z - b.z );
	}
	
	public static vec3f mul( vec3f a, float scalar )
	{
		return new vec3f( a.x * scalar, a.y * scalar, a.z * scalar );
	}
	
	public static vec3f average( List<vec3f> vectors )
	{
		vec3f average = new vec3f();
		
		for( vec3f vec : vectors )
		{
			average.x += vec.x;
			average.y += vec.y;
			average.z += vec.z;
		}
		
		average.x /= vectors.size();
		average.y /= vectors.size();
		average.z /= vectors.size();
		
		return average;
	}
	
	public static vec3f map( vec2f vector, float value, byte map )
	{
		switch( map )
		{
		default:	
		case XYPLANE: return new vec3f( vector.x, vector.y, value );
		case YZPLANE: return new vec3f( value, vector.x, vector.y );
		case XZPLANE: return new vec3f( vector.x, value, vector.y );
		case YXPLANE: return new vec3f( vector.y, vector.x, value );
		case ZYPLANE: return new vec3f( value, vector.y, vector.x );
		case ZXPLANE: return new vec3f( vector.y, value, vector.x );
		}
	}
}
