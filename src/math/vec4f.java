package math;

public class vec4f extends vec3f
{
	public static final vec4f ZERO = new vec4f( 0.0f, 0.0f, 0.0f, 0.0f ); 
	
	public float w;
	
	public vec4f()
	{
		super();
		w = 0.0f;
	}
	
	public vec4f( float scalar )
	{
		super( scalar );
		w = scalar;
	}
	
	public vec4f( float x, float y, float z, float w )
	{
		super( x, y, z );
		this.w = w;
	}
	
	public vec4f( String[] elements )
	{
		super( elements );
		if( elements.length > 4 )
			w = Float.parseFloat( elements[4] );
		else
			w = 0.0f;
	}
	
	public void add( vec4f vector )
	{
		x += vector.x;
		y += vector.y;
		z += vector.z;
		w += vector.w;
	}
	
	public void mul( float scalar )
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;
		w *= scalar;
	}
	
	public float greyValue()
	{
		return ( x + y + z + w ) / 4.0f;
	}
	
	public float dot( vec4f vec )
	{
		return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
	}
	
	public vec4f projectOn( vec4f vector )
	{
		return vec4f.mul( vector, dot( vector ) / vector.dot( vector ) );
	}
	
	public int toRGBA()
	{
		int ia = w > 1.0f ? 0xFF000000 : (w < 0.0f ? 0 : (int)Math.round( w * 255.0f ) << 24 );
		int ir = x > 1.0f ? 0xFF0000 : (x < 0.0f ? 0 : (int)Math.round( x * 255.0f ) << 16 );
		int ig = y > 1.0f ? 0xFF00 : (y < 0.0f ? 0 : (int)Math.round( y * 255.0f ) << 8 );
		int ib = z > 1.0f ? 0xFF : (z < 0.0f ? 0 : (int)Math.round( z * 255.0f ) );
		return ia | ir | ig | ib;
	}
	
	public float length()
	{
		return (float)Math.sqrt( x * x + y * y + z * z + w * w );
	}
	
	public float length2()
	{
		return x * x + y * y + z * z + w * w;
	}
	
	public vec4f unit()
	{
		return mul( this, 1.0f / length() );
	}
	
	@Override
	public String toString()
	{
		return super.toString() + ", " + w;
	}
	
	public static vec4f sub( vec4f a, vec4f b )
	{
		return new vec4f( a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w );
	}
	
	public static vec4f mul( vec4f a, float scalar )
	{
		return new vec4f( a.x * scalar, a.y * scalar, a.z * scalar, a.w * scalar );
	}
	
	public static vec4f createVector( int color )
	{
		return new vec4f( ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f,
			(color & 0xFF) / 255.0f, ((color >> 24) & 0xFF) / 255.0f );
	}
	
	public static vec4f average( vec4f ... vectors )
	{
		vec4f average = new vec4f();
		
		for( vec4f vec : vectors )
		{
			average.x += vec.x;
			average.y += vec.y;
			average.z += vec.z;
			average.w += vec.w;
		}
		
		average.x /= vectors.length;
		average.y /= vectors.length;
		average.z /= vectors.length;
		average.w /= vectors.length;
		return average;
	}
	
	public static vec4f average( vec4f v0, vec4f v1, float w)
	{
		float rw = 1.0f - w;
		return new vec4f(
				v0.x * rw + v1.x * w,
				v0.y * rw + v1.y * w,
				v0.z * rw + v1.z * w,
				v0.w * rw + v1.w * w
		);
	}
	
	public static vec4f average( vec4f v0, vec4f v1, vec4f v2, vec4f v3, float w0, float w1)
	{
		float w2 = w1 - w0 * w1;
		float w3 = w0 * w1;
		
		float rw1 = 1.0f - w1;
		w1 = w0 * rw1;
		w0 = rw1 - w0 * rw1;

		return new vec4f(
				v0.x * w0 + v1.x * w1 + v2.x * w2 + v3.x * w3,
				v0.y * w0 + v1.y * w1 + v2.y * w2 + v3.y * w3,
				v0.z * w0 + v1.z * w1 + v2.z * w2 + v3.z * w3,
				v0.w * w0 + v1.w * w1 + v2.w * w2 + v3.w * w3
		);
	}
	
	public static vec4f random( float min, float max )
	{
		float diff = max - min;
		return new vec4f( 
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff );
	}
}
