package swb.math;

public class vec2f
{
	public static final byte XYPLANE = 0;
	public static final byte YZPLANE = 1;
	public static final byte XZPLANE = 2;
	public static final byte YXPLANE = 3;
	public static final byte ZYPLANE = 4;
	public static final byte ZXPLANE = 5;
	public static final vec2f ZERO = new vec2f( 0.0f, 0.0f ); 
	
	public float x;
	public float y;
	
	public vec2f()
	{
		this.x = 0.0f;
		this.y = 0.0f;
	}
	
	public vec2f( float scalar )
	{
		this.x = scalar;
		this.y = scalar;
	}
	
	public vec2f( float x, float y )
	{
		this.x = x;
		this.y = y;
	}
	
	public vec2f( String[] elements )
	{
		x = Float.parseFloat( elements[1] );
		y = Float.parseFloat( elements[2] );
	}
	
	public void add( vec2f vector )
	{
		x += vector.x;
		y += vector.y;
	}
	
	public void sub( vec2f vector )
	{
		x -= vector.x;
		y -= vector.y;
	}
	
	public void mul( float scalar )
	{
		x *= scalar;
		y *= scalar;
	}
	
	public void div( float scalar )
	{
		x /= scalar;
		y /= scalar;
	}
	
	public float dot( vec2f vec )
	{
		return x * vec.x + y * vec.y;
	}
	
	public vec3f cross( vec2f vec )
	{
		return new vec3f( 0.0f, 0.0f, x * vec.y - y * vec.x );
	}
	
	public vec2f projectOn( vec2f vector )
	{
		return vec2f.mul( vector, dot( vector ) / vector.dot( vector ) );
	}
	
	public float length()
	{
		return (float)Math.sqrt( x * x + y * y );
	}
	
	public float length2()
	{
		return x * x + y * y;
	}
	
	public vec2f unit()
	{
		return mul( this, 1.0f / length() );
	}
	
	public float get( int i )
	{
		switch( i )
		{
		case 0: return x;
		case 1: return y;
		default: return 0.0f;
		}
	}
	
	@Override
	public vec2f clone()
	{
		return new vec2f( x, y );
	}
	
	@Override
	public String toString()
	{
		return x + ", " + y;
	}
	
	public static vec2f sub( vec2f a, vec2f b )
	{
		return new vec2f( a.x - b.x, a.y - b.y );
	}
	
	public static vec2f mul( vec2f a, float scalar )
	{
		return new vec2f( a.x * scalar, a.y * scalar );
	}
	
	public static vec2f average( vec2f ... vectors )
	{
		vec2f average = new vec2f();
		
		for( vec2f vec : vectors )
		{
			average.x += vec.x;
			average.y += vec.y;
		}
		
		average.x /= vectors.length;
		average.y /= vectors.length;
		return average;
	}
}
