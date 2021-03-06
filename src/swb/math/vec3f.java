package swb.math;

import java.util.List;

public class vec3f extends vec2f
{
	public static final vec3f UNDEFINED = new vec3f( 0.0f, 0.0f, 0.0f ); 
	public static final vec3f ZERO = new vec3f( 0.0f, 0.0f, 0.0f ); 
	
	public float z;
	
	public vec3f()
	{
		super();
		this.z = 0.0f;
	}
	
	public vec3f( vec3f copy )
	{
		super( copy.x, copy.y );
		this.z = copy.z;
	}
	
	public vec3f( float scalar )
	{
		super( scalar );
		this.z = scalar;
	}
	
	public vec3f( float x, float y, float z )
	{
		super( x, y );
		this.z = z;
	}
	
	public vec3f( String[] elements )
	{
		super( elements );
		if( elements.length > 3 )
			z = Float.parseFloat( elements[3] );
		else
			z = 0.0f;
	}
	
	public void set( vec3f vector )
	{
		x = vector.x;
		y = vector.y;
		z = vector.z;
	}
	
	public void add( vec3f vector )
	{
		x += vector.x;
		y += vector.y;
		z += vector.z;
	}
	
	public void sub( vec3f vector )
	{
		x -= vector.x;
		y -= vector.y;
		z -= vector.z;
	}
	
	public void mul( float scalar )
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	
	public void div( float scalar )
	{
		x /= scalar;
		y /= scalar;
		z /= scalar;
	}
	
	public float dot( vec3f vec )
	{
		return x * vec.x + y * vec.y + z * vec.z;
	}
	
	public float dot( float x, float y, float z )
	{
		return x * this.x + y * this.y + z * this.z;
	}
	
	public vec3f cross( vec3f vec )
	{
		return new vec3f( 
				y * vec.z - z * vec.y, 
				z * vec.x - x * vec.z, 
				x * vec.y - y * vec.x
		);
	}
	
	public void normalize()
	{
		div( length() );
	}
	
	public vec3f normalized()
	{
		return vec3f.mul( this, 1.0f / length() );
	}
	
	public vec3f projectOn( vec3f vector )
	{
		return vec3f.mul( vector, dot( vector ) / vector.dot( vector ) );
	}
	
	@Override
	public float get( int i )
	{
		switch( i )
		{
		case 0: return x;
		case 1: return y;
		case 2: return z;
		default: return 0.0f;
		}
	}
	
	@Override
	public float length()
	{
		return (float)Math.sqrt( x * x + y * y + z * z );
	}
	
	@Override
	public float length2()
	{
		return x * x + y * y + z * z;
	}
	
	@Override
	public vec3f unit()
	{
		return mul( this, 1.0f / length() );
	}
	
	@Override
	public String toString()
	{
		return super.toString() + ", " + z;
	}
	
	public boolean equals( vec3f v, float threshold )
	{
		return Math.abs( v.x - x ) < threshold && Math.abs( v.y - y ) < threshold && Math.abs( v.z - z ) < threshold;
	}
	
	public static vec3f add( vec3f a, vec3f b )
	{
		return new vec3f( a.x + b.x, a.y + b.y, a.z + b.z );
	}
	
	public static vec3f sub( vec3f a, vec3f b )
	{
		return new vec3f( a.x - b.x, a.y - b.y, a.z - b.z );
	}
	
	public static vec3f mul( vec3f a, float scalar )
	{
		return new vec3f( a.x * scalar, a.y * scalar, a.z * scalar );
	}
	
	public static vec3f average( vec3f ... vectors )
	{
		vec3f average = new vec3f();
		
		for( vec3f vec : vectors )
		{
			average.x += vec.x;
			average.y += vec.y;
			average.z += vec.z;
		}
		
		average.x /= vectors.length;
		average.y /= vectors.length;
		average.z /= vectors.length;
		return average;
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
	
	public static vec3f random( float min, float max )
	{
		float diff = max - min;
		return new vec3f( 
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff );
	}
}
